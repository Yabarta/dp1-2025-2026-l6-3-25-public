import React from 'react';
import GlassPanel from './GlassPanel';


export default function Podium({ players }) {
  // Aseguramos que existan al menos 3 jugadores
    const first = players[0];
    const second = players[1];
    const third = players[2];

    if (!first) return null;

    return (
        <section className="podium-section">
        {/* Puesto 2 */}
        <div className="podium-item rank-2">
            <div className="text-white text-mono" style={{marginBottom: '10px'}}>#02</div>
            <GlassPanel className="avatar-container">
            <img src={second?.avatar} alt="" className="avatar-img" />
            </GlassPanel>
            <div style={{textAlign: 'center', marginTop: '10px'}}>
            <strong className='text-white'>{second?.name}</strong>
            <div className="text-mono text-green" style={{fontSize: '0.8rem'}}>Puntuación: {second?.contamination}</div>
            </div>
        </div>

        {/* Puesto 1 */}
        <div className="podium-item rank-1">
            <div style={{ fontSize: '2rem', marginBottom: '-10px', zIndex: 20 }}>👑</div>
            <GlassPanel className="avatar-container">
            <img src={first.avatar} alt="" className="avatar-img" />
            </GlassPanel>
            <GlassPanel style={{ padding: '8px 20px', borderRadius: '20px', marginTop: '15px', background: 'rgba(234, 179, 8, 0.1)', border: '1px solid rgba(234, 179, 8, 0.3)' }}>
            <div className="text-gold" style={{fontWeight: 'bold'}}>{first.name}</div>
            <div className='text-white-50' style={{fontSize: '0.6rem', letterSpacing: '2px', textTransform: 'uppercase'}}>Puntuación: {first?.contamination}</div>
            </GlassPanel>
        </div>

        {/* Puesto 3 */}
        <div className="podium-item rank-3">
            <div className="text-mono text-white" style={{marginBottom: '10px'}}>#03</div>
            <GlassPanel className="avatar-container">
            <img src={third?.avatar} alt="" className="avatar-img" />
            </GlassPanel>
            <div style={{textAlign: 'center', marginTop: '10px'}}>
            <strong className='text-white'>{third?.name}</strong>
            <div className="text-mono text-green" style={{fontSize: '0.8rem'}}>Puntuación: {third?.contamination}</div>
            </div>
        </div>
        </section>
    );
}