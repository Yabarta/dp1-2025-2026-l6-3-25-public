import React from 'react';
import { useNavigate } from 'react-router-dom';

const modalOverlayStyle = {
  position: 'fixed',
  top: 0,
  left: 0,
  width: '100%',
  height: '100%',
  backgroundColor: 'rgba(0, 0, 0, 0.6)',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  zIndex: 1000,
};

const modalContentStyle = {
  backgroundColor: '#fff',
  padding: '2rem',
  borderRadius: '8px',
  textAlign: 'center',
  boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)',
  width: 'clamp(300px, 40vw, 500px)',
};

const buttonStyle = {
  marginTop: '1.5rem',
  padding: '0.8rem 1.5rem',
  borderRadius: '6px',
  border: 'none',
  backgroundColor: '#c44923ff',
  color: 'white',
  fontWeight: 600,
  cursor: 'pointer',
  fontSize: '1rem',
};

export default function ModalWinner({ winner, onGoToMenu }) {
  const navigate = useNavigate();

  const handleGoToMenu = () => {
    if (onGoToMenu) {
      onGoToMenu();
    } else {
      navigate('/'); // Fallback to home
    }
  };

  if (!winner) {
    return null;
  }

  return (
    <div style={modalOverlayStyle}>
      <div style={modalContentStyle}>
        <h2>¡Partida Terminada!</h2>
        <p>Ha ganado el jugador: <strong>{winner}</strong></p>
        <button style={buttonStyle} onClick={handleGoToMenu}>
          Volver al Menú Principal
        </button>
      </div>
    </div>
  );
}
