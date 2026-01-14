import React from 'react';
import GlassPanel from './GlassPanel';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import bacteria from '../../static/images/bacteria.png';


export default function Leaderboard({ players, selectedIds, jwt, onToggle }) {
    const navigate = useNavigate()

    const handleNavigateToProfile = async (nickname) => {
        if (!jwt) {
            toast.error('Debes iniciar sesión');
            navigate('/login');
            return;
        }
        try {
            const res = await fetch(`/api/v1/players/nickname/${encodeURIComponent(nickname)}`);
            if (!res.ok) throw new Error('Error al buscar usuario');

            navigate(`/profile/${encodeURIComponent(nickname)}`);
        } catch (err) {
            console.error('No se pudo abrir el perfil', err);
            toast.error('No se pudo abrir el perfil');
        }
    };

    return (
        <main className="table-container">
        <GlassPanel style={{ overflow: 'hidden' }}>
            {/* Cabecera */}
            <div className="data-header">
            <div style={{textAlign: 'center'}}>#</div>
            <div>Jugador</div>
            <div style={{textAlign: 'center'}}>Partidas Jugadas</div>
            <div className="hide-mobile" style={{textAlign: 'center'}}>Sarcinas</div>
            <div className="hide-mobile" style={{textAlign: 'center'}}>Porcentaje de victorias</div>
            <div style={{textAlign: 'center'}}>Puntuación</div>
            </div>

            {/* Filas */}
            {players.map((player) => {
            const isSelected = selectedIds.includes(player.id);
            
            return (
                <div 
                    key={player.id}    
                    className={`data-row ${isSelected ? 'selected' : ''}`} 
                    onClick={() => handleNavigateToProfile(player.nickname)}
                >
                    <div className="text-mono text-white" style={{textAlign: 'center'}}>{player.rankingPosition}</div>
                    
                    <div className="player-info">
                        <img src={player.profilePicture || bacteria} alt="" />
                        <span 
                            className='text-white'
                            style={{ fontWeight: isSelected ? 'bold' : 'normal', color: isSelected ? 'var(--primary-green)' : 'inherit' }}>
                        {player.nickname}
                        </span>
                    </div>

                    <div className={"text-mono text-gold"} style={{textAlign: 'center', fontWeight: 'bold'}}>
                        {player.partidasJugadas}
                    </div>
                    
                    <div className="hide-mobile text-mono text-red" style={{textAlign: 'center'}}>{player.sarcinasCreadas}</div>
                    <div className="hide-mobile text-mono text-green" style={{textAlign: 'center'}}>{(player.partidasGanadas / player.partidasJugadas * 100).toFixed(2)}%</div>
                    <div className="hide-mobile text-mono text-blue" style={{textAlign: 'center'}}>{player.score.toFixed(2)}</div>
                </div>
            );
            })}
        </GlassPanel>
        </main>
    );
}