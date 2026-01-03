import React, { useState } from "react";
import HeaderMetrics from "../components/HeaderMetrics";
import Podium from "../components/Podium";
import LeaderboardTable from "../components/LeaderBoardTable";
import ComparatorDock from "../components/ComparatorDock";
import '../styles/PetrisStats.css';

import useFetchState from "../../util/useFetchState";
import tokenService from "../../services/token.service";



const SCIENTISTS_DATA = [
    { id: 'p1', name: 'Dr. Pasteur', rank: 1, contamination: 12, sarcinas: 45, winRate: '88%', avatar: 'https://i.pravatar.cc/150?u=sci1' },
    { id: 'p2', name: 'Prof. Curie', rank: 2, contamination: 14, sarcinas: 38, winRate: '78%', avatar: 'https://i.pravatar.cc/150?u=sci2' },
    { id: 'p3', name: 'Bio_Hazard', rank: 3, contamination: 18, sarcinas: 41, winRate: '72%', avatar: 'https://i.pravatar.cc/150?u=sci3' },
    { id: 'p4', name: 'Lab_Rat_99', rank: 4, contamination: 22, sarcinas: 30, winRate: '65%', avatar: 'https://i.pravatar.cc/150?u=sci4' },
    { id: 'p5', name: 'Bacteria_Boss', rank: 5, contamination: 28, sarcinas: 25, winRate: '55%', avatar: 'https://i.pravatar.cc/150?u=sci5' },
    { id: 'p6', name: 'Agar_King', rank: 6, contamination: 35, sarcinas: 15, winRate: '42%', avatar: 'https://i.pravatar.cc/150?u=sci6' },
];

const jwt = tokenService.getLocalAccessToken()

export default function StatisticRanking() {
  const [message, setMessage] = useState(null)
  const [visible, setVisible] = useState(false)

  const [globalStats, setGlobalStats] = useFetchState(
    [],
    `/api/v1/statistics/global`,
    jwt,
    setMessage,
    setVisible
  )

  const [leaderboards, setLeaderBoard] = useFetchState(
    [],
    `/api/v1/statistics/global/ranking`,
    jwt,
    setMessage,
    setVisible
  )

    const [selectedScientists, setSelectedScientists] = useState([]);

  // Lógica de Negocio: Añadir/Quitar jugadores al comparador
  const toggleSelection = (scientist) => {
    const isSelected = selectedScientists.find(s => s.id === scientist.id);

    if (isSelected) {
      // Si ya está, lo quitamos
      setSelectedScientists(prev => prev.filter(s => s.id !== scientist.id));
    } else {
      // Si no está, lo añadimos (controlando máximo 2)
      if (selectedScientists.length < 2) {
        setSelectedScientists(prev => [...prev, scientist]);
      } else {
        alert("Solo puedes comparar 2 muestras biológicas a la vez.");
      }
    }
  };

  const clearSelection = () => setSelectedScientists([]);

  return (
    <div className="petris-container">
      {/* Fondo Decorativo */}
      <div className="bio-background"></div>

      {/* Cabecera */}
      <HeaderMetrics 
        gamesSize={globalStats.totalGamesPlayed}
        timePlayed={globalStats.totalTimePlayed}
        sarcines={globalStats.totalSarcinasCreated}
        playersRegistered={globalStats.totalPlayers}
      />

      {/* Top 3 */}
      <Podium players={SCIENTISTS_DATA.slice(0, 3)} />

      {/* Tabla del Ranking */}
      <LeaderboardTable 
        players={SCIENTISTS_DATA.slice(3)} 
        selectedIds={selectedScientists.map(s => s.id)} 
        onToggle={toggleSelection} 
      />

      {/* Barra Inferior Flotante */}
      <ComparatorDock 
        selectedPlayers={selectedScientists} 
        onClear={clearSelection} 
      />
    </div>
  );
}