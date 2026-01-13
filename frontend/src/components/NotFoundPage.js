import React from 'react';
import '../static/css/home/NotFound.css'; // Importamos el archivo CSS

const NotFound = () => {
  
  const handleGoHome = () => {
    // Si usas React Router: navigate('/')
    window.location.href = '/'; 
  };

  return (
    <div className="petris-404-container">
      <div className="petris-bg-glow"></div>

      <div className="petris-card">
        <div className="petris-icon-wrapper">
          <span className="petris-icon">🦠</span>
        </div>

        <h1 className="petris-title">404</h1>
        <h2 className="petris-subtitle">ERROR EN LA MUESTRA</h2>
        
        <p className="petris-text">
          La placa de Petri que buscas ha sido contaminada o no existe en este laboratorio.
        </p>

        <button className="petris-btn" onClick={handleGoHome}>
          ▶ Volver al inicio
        </button>
      </div>

      <div className="petris-footer">
        System Status: Critical Failure
      </div>
    </div>
  );
};

export default NotFound;