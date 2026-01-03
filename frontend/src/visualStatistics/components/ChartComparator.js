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
  const options = {
    chart: {
      polar: true,
      height: 500
    },
    title: {
      text: 'Comparación de jugadores',
      x: -80
    },
    pane: {
      size: '80%'
    },
    xAxis: {
      categories: [
        'Bacterias creadas', 
        'Partidas jugadas', 
        'Partidas ganadas', 
        'Partidas perdidas',
        'Sarcinas creadas', 
        'Tiempo jugado', 
      ],
      tickmarkPlacement: 'on',
      lineWidth: 0
    },
    yAxis: {
      gridLineInterpolation: 'polygon',
      lineWidth: 0,
      min: 0,
      max: maxValue // Asumiendo que stats van de 0 a 10
    },
    tooltip: {
      shared: true,
      pointFormat: '<span style="color:{series.color}">{series.name}: <b>{point.y:,.0f}</b><br/>'
    },
    legend: {
      align: 'right',
      verticalAlign: 'middle',
      layout: 'vertical'
    },
    plotOptions: {
      series: {
        fillOpacity: 0.2
      }
    },
    series: [
      {
        type: 'area',
        name: myName,
        data: myStats,
        color: '#f1c40f',
        fillOpacity: 0.5
      },
      {
        type: 'area',
        name: opponentName,
        data: opponentStats,
        color: 'red',
        fillOpacity: 0.5
      }
    ]
  };

  return <HighchartsReact highcharts={Highcharts} options={options} />;
};
