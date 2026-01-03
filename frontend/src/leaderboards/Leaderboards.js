import React, { useEffect, useState } from 'react';
import { Table } from 'reactstrap';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import tokenService from '../services/token.service';
import './Leaderboards.css';

function Leaderboards() {
  const [ranking, setRanking] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const navigate = useNavigate();

  const handleNavigateToProfile = async (nickname) => {
    const jwt = tokenService.getLocalAccessToken();
    if (!jwt) {
      toast.error('User not logged in');
      navigate('/login');
      return;
    }
    try {
      const res = await fetch(`/api/v1/players/nickname/${encodeURIComponent(nickname)}`);
      if (!res.ok) throw new Error('User lookup failed');
      const user = await res.json();
      navigate(`/profile/${encodeURIComponent(user.username ?? nickname)}`);
    } catch (err) {
      console.error('Unable to go to profile', err);
      toast.error('No se pudo abrir el perfil');
    }
  };

  useEffect(() => {
    const fetchRanking = async () => {
      try {
        const res = await fetch('/api/v1/ranking');
        if (!res.ok) throw new Error('Network response was not ok');
        const data = await res.json();
        // Computamos el score para cada jugador ya que el backend no lo manda
        setRanking(data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchRanking();
  }, []);

  if (loading) return <div className="leaderboards-page"><div className="leaderboards-card">Cargando rankings...</div></div>;

  return (
    <div className="leaderboards-page">
      <div className="leaderboards-card">
        <h2 className="leaderboards-title">Ranking</h2>
        <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '0.75rem' }}>
          <input
            type="search"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Buscar usuario"
            className="form-control"
            style={{ maxWidth: 360 }}
          />
        </div>
        <div className="leaderboards-table-wrapper table-responsive">
          <Table striped className="table-striped">
            <thead>
              <tr>
                <th>Rank</th>
                <th>Nombre</th>
                <th>Partidas Jugadas</th>
                <th>Partidas Ganadas</th>
                <th>Puntuación</th>
              </tr>
            </thead>
            <tbody>
              {ranking
                .filter((p) => p.nickname && p.nickname.toLowerCase().includes(search.toLowerCase()))
                .map((p) => (
                <tr key={p.id} onClick={() => handleNavigateToProfile(p.nickname)} style={{ cursor: 'pointer' }}>
                  <td>{p.rankingPosition}</td>
                  <td>{p.nickname}</td>
                  <td>{p.partidasJugadas}</td>
                  <td>{p.partidasGanadas}</td>
                  <td>{p.score !== null ? p.score.toFixed(2) : 'N/A'}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        </div>
      </div>
    </div>
  );
}

export default Leaderboards;
