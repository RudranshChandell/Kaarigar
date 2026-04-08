import React from 'react';
import Link from 'next/link';
import { Mic, ShieldCheck, Star, MapPin, Search, Menu } from 'lucide-react';

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-slate-50 font-sans text-slate-900">
      {/* Navigation */}
      <nav className="flex items-center justify-between px-6 py-4 bg-white shadow-sm sticky top-0 z-50">
        <h1 className="text-2xl font-bold text-blue-700 tracking-tight">KAARIGAR</h1>
        <div className="hidden md:flex space-x-8 font-medium">
          <a href="#" className="hover:text-blue-600">How it works</a>
          <a href="#" className="hover:text-blue-600">Find Work</a>
          <a href="#" className="hover:text-blue-600">Hire Expert</a>
        </div>
        <div className="flex items-center space-x-4">
          <Link href="/login" className="text-sm font-semibold text-slate-600 hover:text-blue-700">Login</Link>
          <Link href="/signup" className="bg-blue-600 text-white px-5 py-2 rounded-full text-sm font-bold hover:bg-blue-700 transition shadow-lg">
            Join Now
          </Link>
        </div>
      </nav>

      {/* Hero Section */}
      <header className="px-6 pt-16 pb-12 text-center max-w-4xl mx-auto">
        <span className="bg-blue-100 text-blue-700 px-4 py-1 rounded-full text-xs font-bold uppercase tracking-widest">
          India's Skilled Workforce Platform
        </span>
        <h2 className="text-4xl md:text-6xl font-extrabold mt-6 leading-tight">
          Your Skill is your <span className="text-blue-600">Identity.</span>
        </h2>
        <p className="mt-4 text-lg text-slate-600">
          No more middle-men. Build your professional career record and get discovered by thousands of customers directly.
        </p>

        {/* Search Bar with Voice Feature */}
        <div className="mt-10 relative max-w-2xl mx-auto">
          <div className="flex items-center bg-white rounded-2xl shadow-xl p-2 border border-slate-200">
            <div className="flex-1 flex items-center px-4">
              <Search className="text-slate-400 mr-2" size={20} />
              <input 
                type="text" 
                placeholder="Search for Plumbers, Electricians..." 
                className="w-full py-3 outline-none text-slate-700"
              />
            </div>
            <button className="bg-slate-100 p-3 rounded-xl hover:bg-blue-50 text-blue-600 transition">
              <Mic size={24} />
            </button>
            <button className="bg-blue-600 text-white px-6 py-3 rounded-xl font-bold ml-2 hover:bg-blue-700 transition">
              Find
            </button>
          </div>
          <p className="text-xs text-slate-400 mt-3 italic">Try saying: "Electrician near me for house wiring"</p>
        </div>
      </header>

      {/* Trust Features (From your notes) */}
      <section className="bg-white py-16 px-6">
        <div className="max-w-6xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-12">
          <div className="text-center">
            <div className="bg-blue-50 w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-4">
              <ShieldCheck className="text-blue-600" size={32} />
            </div>
            <h3 className="text-xl font-bold mb-2">Verified Identity</h3>
            <p className="text-slate-500 text-sm leading-relaxed">Aadhaar verified profiles that prove you are an expert and build customer trust.</p>
          </div>
          <div className="text-center">
            <div className="bg-green-50 w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-4">
              <Star className="text-green-600" size={32} />
            </div>
            <h3 className="text-xl font-bold mb-2">Reputation Record</h3>
            <p className="text-slate-500 text-sm leading-relaxed">"I have done 500+ jobs with 4.8 rating." Carry your reviews wherever you go.</p>
          </div>
          <div className="text-center">
            <div className="bg-orange-50 w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-4">
              <MapPin className="text-orange-600" size={32} />
            </div>
            <h3 className="text-xl font-bold mb-2">Direct Bidding</h3>
            <p className="text-slate-500 text-sm leading-relaxed">Location-based job alerts. You decide your price and deal directly with the client.</p>
          </div>
        </div>
      </section>

      {/* Simple Stats */}
      <section className="py-12 px-6 bg-slate-900 text-white text-center">
        <div className="flex flex-wrap justify-around max-w-5xl mx-auto gap-8">
          <div><p className="text-3xl font-bold">400M+</p><p className="text-slate-400 text-sm">Workers</p></div>
          <div><p className="text-3xl font-bold">12+</p><p className="text-slate-400 text-sm">Languages</p></div>
          <div><p className="text-3xl font-bold">0%</p><p className="text-slate-400 text-sm">Commission</p></div>
        </div>
      </section>
    </div>
  );
}