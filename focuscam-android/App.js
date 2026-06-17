import React, { useState, useRef } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, Alert } from 'react-native';
import { CameraView, useCameraPermissions } from 'expo-camera';

export default function App() {
  const [permission, requestPermission] = useCameraPermissions();
  const [isStudying, setIsStudying] = useState(false);
  const [startTime, setStartTime] = useState(null);
  const [elapsed, setElapsed] = useState(0);
  const [showCamera, setShowCamera] = useState(false);
  const cameraRef = useRef(null);
  const timerRef = useRef(null);

  if (!permission) {
    return <View />;
  }

  if (!permission.granted) {
    return (
      <View style={styles.container}>
        <Text style={styles.text}>We need camera permission to continue</Text>
        <TouchableOpacity onPress={requestPermission} style={styles.button}>
          <Text style={styles.buttonText}>Grant Permission</Text>
        </TouchableOpacity>
      </View>
    );
  }

  const toggleStudy = async () => {
    if (!isStudying) {
      setShowCamera(true);
      setIsStudying(true);
      setStartTime(Date.now());
      // Start timer
      timerRef.current = setInterval(() => {
        setElapsed(Math.floor((Date.now() - startTime) / 1000));
      }, 1000);
    } else {
      clearInterval(timerRef.current);
      setIsStudying(false);
      setShowCamera(false);

      const duration = Math.floor((Date.now() - startTime) / 1000);
      setElapsed(duration);
      Alert.alert('Session Complete', `You studied for ${duration} seconds!`);
    }
  };

  return (
    <View style={styles.container}>
      {showCamera && (
        <View style={styles.cameraContainer}>
          <CameraView style={styles.camera} ref={cameraRef} />
        </View>
      )}
      <Text style={styles.timerText}>
        {isStudying ? `Time: ${elapsed}s` : 'Press Start to Begin'}
      </Text>
      <TouchableOpacity onPress={toggleStudy} style={styles.button}>
        <Text style={styles.buttonText}>
          {isStudying ? 'Stop Studying' : 'Start Studying'}
        </Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#ffffff', // white background
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    fontSize: 16,
    marginBottom: 10,
    textAlign: 'center',
  },
  timerText: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  button: {
    backgroundColor: '#007AFF',
    paddingVertical: 15,
    paddingHorizontal: 40,
    borderRadius: 10,
  },
  buttonText: {
    color: '#fff',
    fontSize: 18,
    fontWeight: '600',
  },
  cameraContainer: {
    width: 250,
    height: 250,
    borderRadius: 10,
    overflow: 'hidden',
    marginBottom: 20,
  },
  camera: {
    flex: 1,
  },
});
