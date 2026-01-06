import { useState } from 'react';
import '../static/css/home/home.css';
import logo from '../static/images/petris3D_recortado.png';
import tokenService from '../services/token.service.js';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import jwt_decode from "jwt-decode";
import { FaPlay, FaUser, FaTrophy, FaSignOutAlt, FaUsers, FaTimes, FaRegTimesCircle, FaRegCalendarTimes, FaClock } from 'react-icons/fa';

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
   const isPlayer = isLoggedIn ? jwt_decode(jwt).authorities.includes("PLAYER") : false;

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
  const PlayerIndex = () => {return (
              <div className="menu-grid">
                <button className="menu-btn primary" onClick={handlePlay}>
                  <FaPlay /> Jugar Ahora
                </button>
                  <button className="menu-btn" onClick={handleProfile}>
                    <FaUser /> Mi Perfil
                  </button>
                <button className="menu-btn" onClick={() => navigate('/ranking')}>
                  <FaTrophy /> Ranking Global
                </button>
              </div>)
              };
  const AdminIndex = () => {return (
              <div className="menu-grid">
                <button className="menu-btn primary" onClick={() => navigate('/users')}>
                  <FaClock /> Partidas Activas
                </button>
                <button className="menu-btn" onClick={() => navigate('/users')}>
                  <FaUsers /> Usuarios
                </button>
                <button className="menu-btn" onClick={() => navigate('/ranking')}>
                  <FaTrophy /> Ranking Global
                </button>
              </div>)
              };
  return (
    <div className="homePageContainer">
      <div className="mainMenuCard">
        <div className="logo-section">
          <img src={logo} width={180} alt="Petris Logo" className="logo-img" />
          <h1 className="game-title">PETRIS</h1>
          <div className="game-subtitle">Sistema de Defensa Bacteriana</div>
        </div>
        {isLoggedIn ? 
          (isPlayer ? <PlayerIndex /> : <AdminIndex />)
           : (
            <div className="menu-grid">
              <button className="menu-btn primary" onClick={handlePlay}>
                <FaPlay /> Iniciar Sesión para Jugar
              </button>
            </div>
          )
        }
        {isLoggedIn && (
          <div className="menu-footer">
            <button className="icon-btn" onClick={handleLogout} title="Cerrar Sesión">
              <FaSignOutAlt />
            </button>
          </div>
          )
        }
      </div>
    </div>
  );
}
