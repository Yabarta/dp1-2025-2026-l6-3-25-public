import React from 'react';
import GlassPanel from './GlassPanel';


export default function Leaderboard({ players, selectedIds, onToggle }) {
    return (
        <main className="table-container">
        <GlassPanel style={{ overflow: 'hidden' }}>
            {/* Cabecera */}
            <div className="data-header">
            <div style={{textAlign: 'center'}}>#</div>
            <div>Científico</div>
            <div style={{textAlign: 'center'}}>Partidas Jugadas</div>
            <div className="hide-mobile" style={{textAlign: 'center'}}>Sarcinas</div>
            <div className="hide-mobile" style={{textAlign: 'center'}}>Win Rate</div>
            <div style={{textAlign: 'center'}}>Puntuación</div>
            </div>

            {/* Filas */}
            {players.map((player) => {
            const isSelected = selectedIds.includes(player.id);
            
            return (
                <div key={player.id} className={`data-row ${isSelected ? 'selected' : ''}`}>
                <div className="text-mono text-white" style={{textAlign: 'center'}}>{player.rank}</div>
                
                <div className="player-info">
                    <img src={player.avatar} alt="" />
                    <span 
                        className='text-white'
                        style={{ fontWeight: isSelected ? 'bold' : 'normal', color: isSelected ? 'var(--primary-green)' : 'inherit' }}>
                    {player.name}
                    </span>
                </div>

                <div className={`text-mono ${player.contamination > 20 ? 'text-white' : ''}`} style={{textAlign: 'center', fontWeight: 'bold'}}>
                    {player.contamination}%
                </div>
                
                <div className="hide-mobile text-mono text-red" style={{textAlign: 'center'}}>{player.sarcinas}</div>
                <div className="hide-mobile text-mono text-green" style={{textAlign: 'center'}}>{player.winRate}</div>
                <div className="hide-mobile text-mono text-red" style={{textAlign: 'center'}}>{player.contamination}</div>
                </div>
            );
            })}
        </GlassPanel>
        </main>
    );
}