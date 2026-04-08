"use client";
import React, { useState } from 'react';
import { auth } from '@/lib/firebase';
import { RecaptchaVerifier, signInWithPhoneNumber } from "firebase/auth";
import { useRouter } from 'next/navigation';
import { HardHat, User, Phone, ShieldCheck } from 'lucide-react';

export default function LoginPage() {
  const [phone, setPhone] = useState("");
  const [otp, setOtp] = useState("");
  const [step, setStep] = useState("phone"); // 'phone' or 'otp'
  const [role, setRole] = useState("worker"); // User's intent
  const [confirmationResult, setConfirmationResult] = useState(null);
  const router = useRouter();

  // 1. Initialize Recaptcha
  const setupRecaptcha = () => {
    if (!window.recaptchaVerifier) {
      window.recaptchaVerifier = new RecaptchaVerifier(auth, 'recaptcha-container', {
        'size': 'invisible'
      });
    }
  };

  // 2. Send OTP
  const sendOtp = async (e) => {
    e.preventDefault();
    setupRecaptcha();
    const appVerifier = window.recaptchaVerifier;
    try {
      const confirmation = await signInWithPhoneNumber(auth, `+91${phone}`, appVerifier);
      setConfirmationResult(confirmation);
      setStep("otp");
    } catch (error) {
      console.error("SMS Error", error);
      alert("Failed to send SMS. Check phone number.");
    }
  };

  // 3. Verify OTP & Redirect
  const verifyOtp = async (e) => {
    e.preventDefault();
    try {
      const result = await confirmationResult.confirm(otp);
      const user = result.user;

      // CALL YOUR SPRING BOOT API HERE to check/set role
      // const res = await axios.post('/api/auth/verify', { uid: user.uid, role: role });
      
      // For now, simple redirect based on state
      if (role === "worker") {
        router.push("/dashboard/worker");
      } else {
        router.push("/dashboard/customer");
      }
    } catch (error) {
      alert("Invalid OTP");
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-6">
      <div id="recaptcha-container"></div>
      
      <div className="w-full max-w-md bg-white rounded-3xl shadow-2xl overflow-hidden border border-slate-100">
        <div className="bg-blue-600 p-8 text-center text-white">
          <h1 className="text-3xl font-black italic tracking-tighter">KAARIGAR</h1>
          <p className="text-blue-100 text-sm mt-1">Direct Work. Direct Pay. No Middlemen.</p>
        </div>

        <div className="p-8">
          {step === "phone" ? (
            <>
              <h2 className="text-xl font-bold text-center mb-6 text-slate-800">Choose your account type</h2>
              
              {/* Role Toggle */}
              <div className="grid grid-cols-2 gap-4 mb-8">
                <button 
                  onClick={() => setRole("worker")}
                  className={`p-4 rounded-2xl border-2 flex flex-col items-center transition ${role === 'worker' ? 'border-blue-600 bg-blue-50 text-blue-700' : 'border-slate-100 text-slate-400'}`}
                >
                  <HardHat size={32} />
                  <span className="text-xs font-bold mt-2">I AM A WORKER</span>
                </button>
                <button 
                  onClick={() => setRole("customer")}
                  className={`p-4 rounded-2xl border-2 flex flex-col items-center transition ${role === 'customer' ? 'border-blue-600 bg-blue-50 text-blue-700' : 'border-slate-100 text-slate-400'}`}
                >
                  <User size={32} />
                  <span className="text-xs font-bold mt-2">I WANT TO HIRE</span>
                </button>
              </div>

              <form onSubmit={sendOtp} className="space-y-4">
                <div className="relative">
                  <Phone className="absolute left-4 top-4 text-slate-400" size={20} />
                  <span className="absolute left-11 top-4 text-slate-800 font-semibold">+91</span>
                  <input 
                    type="tel" 
                    onChange={(e) => setPhone(e.target.value)}
                    placeholder="Mobile Number" 
                    className="w-full pl-20 pr-4 py-4 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-600"
                    required
                  />
                </div>
                <button className="w-full bg-blue-600 text-white py-4 rounded-xl font-bold text-lg hover:bg-blue-700 transition shadow-lg">
                  Send OTP
                </button>
              </form>
            </>
          ) : (
            <form onSubmit={verifyOtp} className="space-y-6 text-center">
              <ShieldCheck className="mx-auto text-blue-600" size={48} />
              <h2 className="text-xl font-bold">Enter OTP sent to +91 {phone}</h2>
              <input 
                type="text" 
                onChange={(e) => setOtp(e.target.value)}
                placeholder="6-Digit Code" 
                className="w-full text-center text-2xl tracking-[1em] py-4 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-600"
                maxLength={6}
                required
              />
              <button className="w-full bg-green-600 text-white py-4 rounded-xl font-bold text-lg hover:bg-green-700 transition">
                Verify & Continue
              </button>
              <button onClick={() => setStep("phone")} className="text-blue-600 text-sm font-semibold">
                Change Phone Number
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}