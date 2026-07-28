import React from 'react';
import { useTranslation } from 'react-i18next';

export default function LanguageSwitcher() {
  const { i18n } = useTranslation();

  const toggleLanguage = () => {
    const newLanguage = i18n.language === 'en' ? 'hi' : 'en';
    i18n.changeLanguage(newLanguage);
    localStorage.setItem('language', newLanguage);
  };

  return (
    <button
      onClick={toggleLanguage}
      className="px-4 py-2 rounded-lg border border-[#1d4ed8] text-[#1d4ed8] hover:bg-[#1d4ed8] hover:text-white transition-colors font-medium"
      aria-label="Toggle language"
    >
      {i18n.language === 'en' ? 'हिंदी' : 'English'}
    </button>
  );
}
