// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
const firebaseConfig = {
  apiKey: "AIzaSyDXqSZ07csDswASfMRM8nMS2MbzTSVsCIs",
  authDomain: "kaarigar-8fa18.firebaseapp.com",
  projectId: "kaarigar-8fa18",
  storageBucket: "kaarigar-8fa18.firebasestorage.app",
  messagingSenderId: "307114032887",
  appId: "1:307114032887:web:455cf28897695455e51df2",
  measurementId: "G-NTF87B0KYW"
};
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);