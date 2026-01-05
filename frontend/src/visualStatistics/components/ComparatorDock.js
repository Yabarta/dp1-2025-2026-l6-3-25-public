import React from 'react';
import bacteria from '../../static/images/bacteria.png';

export default function ComparatorDock({ selectedPlayers, onClear }) {
    const isOpen = selectedPlayers.length > 0;
    const isReady = selectedPlayers.length === 2;

  return (
    <div className={`comparator-dock ${isOpen ? 'open' : ''}`}>
        <div className="dock-content">
            <div style={{display: 'flex', alignItems: 'center'}}>
            <div className="hide-mobile">
                <div style={{color: 'var(--primary-green)', fontSize: '0.7rem', fontWeight: 'bold', textTransform: 'uppercase', letterSpacing: '2px'}}>Comparador</div>
                <div className="text-mono" style={{fontSize: '0.9rem'}}>{selectedPlayers.length} / 2 Sujetos</div>
            </div>

            <div className="avatars-area">
                {[0, 1].map((idx) => (
                selectedPlayers[idx] ? (
                    <img key={idx} src={selectedPlayers[idx].profilePicture || bacteria} className="dock-avatar" alt="" />
                ) : (
                    <div key={idx} className="dock-placeholder">{idx + 1}</div>
                )
                ))}
            </div>
            
            {selectedPlayers.length > 0 && (
                <button onClick={onClear} style={{background: 'none', border: 'none', color: '#ef4444', marginLeft: '15px', cursor: 'pointer', fontSize: '0.8rem', textDecoration: 'underline'}}>
                Limpiar
                </button>
            )}
            </div>

            <button 
            disabled={!isReady}
            className={`btn-compare ${isReady ? 'ready' : ''}`}
            >
            Analizar
            </button>
        </div>
        </div>
    );
}