import React from 'react';
import Highcharts from 'highcharts';
import HighchartsReact from 'highcharts-react-official';
import HighchartsMore from 'highcharts/highcharts-more';

if (typeof HighchartsMore === 'function') {
    HighchartsMore(Highcharts);
} else if (HighchartsMore && typeof HighchartsMore.default === 'function') {
    HighchartsMore.default(Highcharts);
}

export default function ChartComparator({ myName, myStats, opponentStats, opponentName, maxValue }){
  
  const themeColors = {
      text: '#c0c0c0',
      grid: '#235e6f',
      playerColor: '#00ffd599',
      opponentColor: 'rgba(255, 50, 50, 0.6)',
      font: "'Courier Prime', 'Courier New', monospace" 
  };

  const options = {
    chart: {
      polar: true,
      height: 500,
      backgroundColor: 'transparent',
      style: {
          fontFamily: themeColors.font
      }
    },
    title: {
      text: 'RADAR DE RENDIMIENTO',
      style: {
          color: '#ffffff',
          textTransform: 'uppercase',
          fontSize: '20px',
          letterSpacing: '2px'
      },
      y: 20
    },
    pane: {
      size: '80%',
      background: {
          backgroundColor: 'transparent',
          borderWidth: 0
      }
    },
    xAxis: {
      categories: [
        'Media bacterias creadas', 
        'Partidas jugadas', 
        'Partidas ganadas', 
        'Partidas perdidas',
        'Sarcinas creadas', 
        'Tiempo jugado(h)', 
      ],
      tickmarkPlacement: 'on',
      lineWidth: 0,
      labels: {
          style: {
              color: '#00ff9d',
              fontSize: '11px',
              fontWeight: 'bold'
          }
      },
      gridLineColor: themeColors.grid
    },
    yAxis: {
      gridLineInterpolation: 'polygon',
      lineWidth: 0,
      min: 0,
      max: maxValue,
      gridLineColor: themeColors.grid,
      labels: {
          style: {
              color: themeColors.text
          }
      }
    },
    tooltip: {
      shared: true,
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: '#00ff9d',
      style: {
          color: '#fff'
      },
      pointFormat: '<span style="color:{series.color}">● {series.name}: <b>{point.y:,.0f}</b><br/>'
    },
    legend: {
      align: 'center',
      verticalAlign: 'bottom',
      layout: 'horizontal',
      itemStyle: {
          color: '#fff',
          fontWeight: 'bold'
      },
      itemHoverStyle: {
          color: '#00ff9d'
      }
    },
    plotOptions: {
      series: {
        fillOpacity: 0.2,
        marker: {
            radius: 4,
            lineWidth: 2,
            lineColor: null 
        }
      }
    },
    series: [
      {
        type: 'area',
        name: myName || 'Yo',
        data: myStats,
        color: '#00ffd599',
        fillColor: themeColors.playerColor,
        pointPlacement: 'on'
      },
      {
        type: 'area',
        name: opponentName || 'Rival',
        data: opponentStats,
        color: '#ff4444',
        fillColor: themeColors.opponentColor,
        pointPlacement: 'on'
      }
    ],
    credits: {
        enabled: false
    }
  };

  return <HighchartsReact highcharts={Highcharts} options={options} />;
};