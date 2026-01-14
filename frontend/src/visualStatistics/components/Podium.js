import React from 'react';
import GlassPanel from './GlassPanel';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import bacteria from '../../static/images/bacteria.png';


export default function Podium({ players, jwt }) {

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

    const first = players[0];
    const second = players[1];
    const third = players[2];

    if (!first) return null;

    return (
        <section className="podium-section">
        {/* Puesto 2 */}
        <div className="podium-item rank-2" onClick={() => { handleNavigateToProfile(second.nickname) }}>
            <div className="text-white text-mono" style={{marginBottom: '10px'}}>#02</div>
                <GlassPanel className="avatar-container">
                <img src={second?.profilePicture || bacteria} alt="" className="avatar-img" />
                </GlassPanel>
                <div style={{textAlign: 'center', marginTop: '10px'}}>
                <strong className='text-white'>{second?.nickname}</strong>
                <div className="text-mono text-green" style={{fontSize: '0.8rem'}}>Puntuación: {second?.score.toFixed(2)}</div>
            </div>
        </div>

        {/* Puesto 1 */}
        <div className="podium-item rank-1" onClick={() => { handleNavigateToProfile(first.nickname) }}>
            <div style={{ fontSize: '2rem', marginBottom: '-10px', zIndex: 20 }}>👑</div>
            <GlassPanel className="avatar-container">
            <img src={first?.profilePicture || bacteria} alt="" className="avatar-img" />
            </GlassPanel>
            <GlassPanel style={{ padding: '8px 20px', borderRadius: '20px', marginTop: '15px', background: 'rgba(234, 179, 8, 0.1)', border: '1px solid rgba(234, 179, 8, 0.3)' }}>
            <div className="text-gold" style={{fontWeight: 'bold'}}>{first.nickname}</div>
            <div className='text-white-50' style={{fontSize: '0.6rem', letterSpacing: '2px', textTransform: 'uppercase'}}>Puntuación: {first?.score.toFixed(2)}</div>
            </GlassPanel>
        </div>

        {/* Puesto 3 */}
        <div className="podium-item rank-3" onClick={() => { handleNavigateToProfile(third.nickname) }}>
            <div className="text-mono text-white" style={{marginBottom: '10px'}}>#03</div>
            <GlassPanel className="avatar-container">
            <img src={third?.profilePicture || bacteria} alt="" className="avatar-img" />
            </GlassPanel>
            <div style={{textAlign: 'center', marginTop: '10px'}}>
            <strong className='text-white'>{third?.nickname}</strong>
            <div className="text-mono text-green" style={{fontSize: '0.8rem'}}>Puntuación: {third?.score.toFixed(2)}</div>
            </div>
        </div>
        </section>
    );
}