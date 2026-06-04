import { useState, useEffect } from 'react';

const DarkModeToggle = () => {
  const [isDarkMode, setIsDarkMode] = useState<boolean>(() => {
    return localStorage.getItem('theme') === 'dark';
  });

  useEffect(() => {
    const html = document.documentElement; 
    
    if (isDarkMode) {
      html.classList.add('dark-mode');
      localStorage.setItem('theme', 'dark');
    } else {
      html.classList.remove('dark-mode');
      localStorage.setItem('theme', 'light');
    }
  }, [isDarkMode]);

  return (
    <button 
      onClick={() => setIsDarkMode(!isDarkMode)} 
      className="btn-dark-mode" 
      title={isDarkMode ? "Cambiar a Modo Claro" : "Cambiar a Modo Oscuro"}
    >
      {isDarkMode ? '☀️' : '🌙'}
    </button>
  );
};

export default DarkModeToggle;