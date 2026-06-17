import 'dart:async';
import 'dart:io' show Platform;

import 'package:camera/camera.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:flutter/material.dart';
import 'package:google_mlkit_face_detection/google_mlkit_face_detection.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Get available cameras before running the app.
  final cameras = await availableCameras();

  runApp(FocusCamApp(cameras: cameras));
}

class FocusCamApp extends StatefulWidget {
  final List<CameraDescription> cameras;

  const FocusCamApp({super.key, required this.cameras});

  @override
  State<FocusCamApp> createState() => _FocusCamAppState();
}

class _FocusCamAppState extends State<FocusCamApp> {
  bool _isDark = false;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'FocusCam',
      themeMode: _isDark ? ThemeMode.dark : ThemeMode.light,
      theme: ThemeData(
        brightness: Brightness.light,
        scaffoldBackgroundColor: Colors.white,
      ),
      darkTheme: ThemeData(
        brightness: Brightness.dark,
        scaffoldBackgroundColor: Colors.black,
      ),
      debugShowCheckedModeBanner: false,
      home: FocusPage(
        cameras: widget.cameras,
        isDark: _isDark,
        toggleTheme: () => setState(() => _isDark = !_isDark),
      ),
    );
  }
}

class FocusPage extends StatefulWidget {
  final List<CameraDescription> cameras;
  final bool isDark;
  final VoidCallback toggleTheme;

  const FocusPage({
    super.key,
    required this.cameras,
    required this.isDark,
    required this.toggleTheme,
  });

  @override
  State<FocusPage> createState() => _FocusPageState();
}

class _FocusPageState extends State<FocusPage> {
  CameraController? _cameraController;
  bool _cameraReady = false;

  int _seconds = 0;
  Timer? _timer;
  bool _isStudying = false;

  // ML Kit
  FaceDetector? _faceDetector;
  bool _faceDetected = true;
  int _graceCounter = 0;
  static const int graceLimit = 3;
  bool _isProcessingFace = false; // avoid overlapping detections

  @override
  void initState() {
    super.initState();

    // Init ML Kit only on mobile, not web.
    if (!kIsWeb && (Platform.isAndroid || Platform.isIOS)) {
      final options = FaceDetectorOptions(
        enableContours: false,
        enableLandmarks: false,
      );
      _faceDetector = FaceDetector(options: options);
    }

    // Delay camera init slightly to avoid iOS race condition.
    Future.delayed(const Duration(milliseconds: 350), _initCamera);
  }

  Future<void> _initCamera() async {
    try {
      if (widget.cameras.isEmpty) {
        print("No cameras found.");
        return;
      }

      print("INITIALIZING CAMERA…");

      _cameraController = CameraController(
        widget.cameras.first,
        ResolutionPreset.medium,
        enableAudio:
            false, // prevent AVAudioSession deadlocks; we don't need audio
      );

      await _cameraController!.initialize().timeout(
        const Duration(seconds: 5),
        onTimeout: () {
          print("CAMERA INIT TIMED OUT");
          return;
        },
      );

      print("CAMERA INITIALIZED");

      if (!mounted) return;

      setState(() => _cameraReady = true);
    } catch (e) {
      print("CAMERA INIT ERROR: $e");
    }
  }

  // Called once per second while studying
  void _tick() {
    setState(() {
      _seconds++;
    });

    _scheduleFaceCheck();
  }

  void _scheduleFaceCheck() {
    if (!_cameraReady) return;
    if (!_isStudying) return;
    if (_isProcessingFace) return;
    if (kIsWeb) return; // web: no ML Kit
    if (!Platform.isAndroid && !Platform.isIOS) return;
    if (_faceDetector == null) return;

    _isProcessingFace = true;
    _captureAndDetectFace().whenComplete(() {
      _isProcessingFace = false;
    });
  }

  Future<void> _captureAndDetectFace() async {
    try {
      if (_cameraController == null || !_cameraController!.value.isInitialized) {
        return;
      }

      // Take a still frame
      final picture = await _cameraController!.takePicture();

      final inputImage = InputImage.fromFilePath(picture.path);
      final faces = await _faceDetector!.processImage(inputImage);

      if (faces.isNotEmpty) {
        _onFaceFound();
      } else {
        _onFaceLost();
      }
    } catch (e) {
      // If anything goes wrong, treat as "no face" but don't crash.
      print("Face detection error: $e");
      _onFaceLost();
    }
  }

  void _onFaceFound() {
    if (!_faceDetected || _graceCounter != 0) {
      setState(() {
        _faceDetected = true;
        _graceCounter = 0;
      });
    } else {
      _faceDetected = true;
      _graceCounter = 0;
    }
  }

  void _onFaceLost() {
    if (_faceDetected) {
      _faceDetected = false;
      _graceCounter = 0;
    }

    _graceCounter++;

    if (_graceCounter >= graceLimit && _isStudying) {
      _stopStudying(auto: true);
    } else {
      setState(() {});
    }
  }

  void _startStudying() {
    _isStudying = true;
    _seconds = 0;
    _graceCounter = 0;

    _timer?.cancel();
    _timer = Timer.periodic(const Duration(seconds: 1), (_) => _tick());

    setState(() {});
  }

  void _stopStudying({bool auto = false}) {
    _timer?.cancel();
    _isStudying = false;

    if (!mounted) return;

    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: Text(auto ? "Paused Automatically" : "Session Complete"),
        content: Text(
          auto
              ? "Face undetected for $graceLimit seconds. Timer paused.\n\nStudied: $_seconds s"
              : "You studied for $_seconds seconds!",
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text("OK"),
          )
        ],
      ),
    );

    setState(() {});
  }

  @override
  void dispose() {
    _timer?.cancel();
    _cameraController?.dispose();
    _faceDetector?.close();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("FocusCam"),
        actions: [
          IconButton(
            icon: Icon(widget.isDark ? Icons.wb_sunny : Icons.dark_mode),
            onPressed: widget.toggleTheme,
          ),
        ],
      ),

      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            const SizedBox(height: 15),

            Text(
              _isStudying ? "Studying..." : "Idle",
              style: const TextStyle(fontSize: 20),
            ),

            const SizedBox(height: 25),

            // CAMERA BOX + OVERLAY
            Container(
              width: 260,
              height: 260,
              decoration: BoxDecoration(
                border: Border.all(color: Colors.indigo, width: 3),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Stack(
                children: [
                  if (_cameraReady)
                    ClipRRect(
                      borderRadius: BorderRadius.circular(10),
                      child: CameraPreview(_cameraController!),
                    )
                  else
                    const Center(child: Text("Loading camera...")),

                  if (_isStudying && !_faceDetected)
                    Container(
                      decoration: BoxDecoration(
                        color: Colors.black.withOpacity(0.55),
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: const Center(
                        child: Text(
                          "NO FACE DETECTED",
                          style: TextStyle(
                            color: Colors.redAccent,
                            fontSize: 20,
                            fontWeight: FontWeight.bold,
                          ),
                          textAlign: TextAlign.center,
                        ),
                      ),
                    ),
                ],
              ),
            ),

            const SizedBox(height: 30),

            Text(
              "Time: $_seconds s",
              style: const TextStyle(fontSize: 24),
            ),

            const SizedBox(height: 30),

            ElevatedButton(
              onPressed: _isStudying ? _stopStudying : _startStudying,
              style: ElevatedButton.styleFrom(
                backgroundColor: _isStudying ? Colors.red : Colors.green,
                padding:
                    const EdgeInsets.symmetric(horizontal: 60, vertical: 18),
              ),
              child: Text(
                _isStudying ? "Stop" : "Start",
                style: const TextStyle(
                  fontSize: 18,
                  color: Colors.white,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
