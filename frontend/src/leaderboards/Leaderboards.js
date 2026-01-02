import React, { useEffect, useState } from 'react';
import { Table } from 'reactstrap';

function Leaderboards() {
  const [ranking, setRanking] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchRanking = async () => {
      try {
        const res = await fetch('/api/v1/ranking');
        if (!res.ok) throw new Error('Network response was not ok');
        const data = await res.json();
        // Backend returns List<Player> without score; compute score here
        const withScores = data.map((p) => {
          const stats = p.statistics || {};
          const gp = stats.gamesPlayed || 0;
          const gw = stats.gamesWon || 0;
          let score = null;
          if (gp >= 10) {
            const winPercent = (gw / gp) * 100.0;
            score = winPercent + 20.0 * Math.log10(gp);
          }
          return {
            id: p.id,
            nickname: p.nickname,
            gamesPlayed: gp,
            gamesWon: gw,
            score: score,
          };
        });

        // Already sorted by backend, but ensure sort here
        withScores.sort((a, b) => (b.score || 0) - (a.score || 0));
        setRanking(withScores);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchRanking();
  }, []);

  if (loading) return <div>Loading leaderboards...</div>;

  return (
    <div className="container-fluid d-flex justify-content-center" style={{ paddingTop: '1rem' }}>
      <div style={{ width: '100%', maxWidth: '1000px' }}>
        <h2 className="text-center">Leaderboards</h2>
        <div style={{ maxHeight: '70vh', overflowY: 'auto', padding: '0.5rem' }}>
          <div className="table-responsive">
            <Table striped>
        <thead>
          <tr>
            <th>#</th>
            <th>Nickname</th>
            <th>Games Played</th>
            <th>Games Won</th>
            <th>Score</th>
          </tr>
        </thead>
        <tbody>
          {ranking.map((p, idx) => (
            <tr key={p.id}>
              <td>{idx + 1}</td>
              <td>{p.nickname}</td>
              <td>{p.gamesPlayed}</td>
              <td>{p.gamesWon}</td>
              <td>{p.score !== null ? p.score.toFixed(2) : 'N/A'}</td>
            </tr>
          ))}
        </tbody>
          </Table>
            </div>
        </div>
      </div>
    </div>
  );
}

export default Leaderboards;
