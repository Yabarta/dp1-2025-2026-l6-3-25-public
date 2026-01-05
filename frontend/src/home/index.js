import { useState } from 'react';
import '../static/css/home/home.css';
import logo from '../static/images/petris3D_recortado.png';
import tokenService from '../services/token.service.js';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import jwt_decode from "jwt-decode";
import { FaPlay, FaUser, FaTrophy, FaSignOutAlt } from 'react-icons/fa';

const jwt = tokenService.getLocalAccessToken();

export default function Home() {
  const navigate = useNavigate();
  const [username] = useState(() => {
    if (!jwt) return "";
    try {
      return jwt_decode(jwt).sub;
    } catch (e) {
      return "";
    }
  });
  
  const isLoggedIn = !!jwt;

  const handlePlay = () => {
    if (!isLoggedIn) {
      navigate('/login');
    } else {
      navigate('/lobby');
    }
  };

  const handleProfile = () => {
    if (!isLoggedIn) {
      toast.error("Debes iniciar sesión");
      navigate('/login');
    } else {
      navigate(`/profile/${username}`);
    }
  };

  const handleLogout = () => {
    navigate('/logout');
  };

  return (
    <div className="homePageContainer">
      <div className="mainMenuCard">
        
        <div className="logo-section">
          <img src={logo} width={180} alt="Petris Logo" className="logo-img" />
          <h1 className="game-title">PETRIS</h1>
          <div className="game-subtitle">Sistema de Defensa Bacteriana</div>
        </div>

        <div className="menu-grid">
          <button className="menu-btn primary" onClick={handlePlay}>
            <FaPlay /> {isLoggedIn ? "Jugar Ahora" : "Iniciar Sesión para Jugar"}
          </button>

          {isLoggedIn && (
            <button className="menu-btn" onClick={handleProfile}>
              <FaUser /> Mi Perfil
            </button>
          )}
          {isLoggedIn && (
          <button className="menu-btn" onClick={() => navigate('/ranking')}>
            <FaTrophy /> Ranking Global
          </button>
          )}
        </div>

        <div className="menu-footer">
          {isLoggedIn && (
            <button className="icon-btn" onClick={handleLogout} title="Cerrar Sesión">
              <FaSignOutAlt />
            </button>
          )}
        </div>

      </div>
    </div>
  );
}
