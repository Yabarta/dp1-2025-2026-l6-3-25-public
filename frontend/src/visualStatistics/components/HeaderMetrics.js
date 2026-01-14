import React from 'react';
import GlassPanel from './GlassPanel';
import { useNavigate } from 'react-router-dom';
import tokenService from '../../services/token.service';
import jwt_decode from "jwt-decode";


export default function Header({ gamesSize, timePlayed, sarcines, playersRegistered }) {

    const navigate = useNavigate()
    const jwt = tokenService.getLocalAccessToken();
    const isPlayer = jwt ? jwt_decode(jwt).authorities.includes("PLAYER") : false;

    const hoursPlayed = Math.floor(timePlayed / 60);

    const navigateToComparator = () => {
        navigate('/comparator')
    }

    return (
        <header className="petris-header">
        <div className="header-top">
            <div className="logo-area">
                <div className="logo-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126zM12 15.75h.007v.008H12v-.008z"/></svg>
                </div>
                <div>
                    <h1 style={{ margin: 0, fontWeight: 800, color: 'white' }}>PETRIS <span className="text-mono text-green">ESTADÍSTICAS</span></h1>
                    <small className="" style={{ letterSpacing: '2px', fontSize: '0.7rem', color: 'white' }}>SISTEMA DE CONTROL</small>
                </div>
            </div>
            {isPlayer && <button class="search-btn" onClick={() => { navigateToComparator() }}>¡¡Compara tus estadísticas!!</button>}
        </div>

        {/* KPI GRID */}
        <div className="kpi-grid">
            <GlassPanel className="kpi-card" style={{ borderLeft: '4px solid var(--alert-gold)' }}>
            <div className="kpi-title">Partidas Jugadas</div>
            <div className="kpi-value text-mono text-gold">{ gamesSize }</div>
            </GlassPanel>
            <GlassPanel className="kpi-card" style={{ borderLeft: '4px solid var(--alert-red)' }}>
            <div className="kpi-title">Sarcinas Totales</div>
            <div className="kpi-value text-mono text-red">{ sarcines }</div>
            </GlassPanel>
            <GlassPanel className="kpi-card" style={{ borderLeft: '4px solid var(--alert-green)' }}>
            <div className="kpi-title">Tiempo Jugado</div>
            <div className="kpi-value text-mono text-green">{ hoursPlayed } horas</div>
            </GlassPanel>
            <GlassPanel className="kpi-card" style={{ borderLeft: '4px solid var(--alert-blue)' }}>
            <div className="kpi-title">Jugadores registrados</div>
            <div className="kpi-value text-mono text-blue">{ playersRegistered }</div>
            </GlassPanel>
        </div>
        </header>
    );
}