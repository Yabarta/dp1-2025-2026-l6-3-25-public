import React from 'react';
import Highcharts from 'highcharts';
import HighchartsReact from 'highcharts-react-official';
import HighchartsMore from 'highcharts/highcharts-more';

// Inicializamos el módulo 'more' para tener acceso a boxplot
if (typeof HighchartsMore === 'function') {
    HighchartsMore(Highcharts);
} else if (HighchartsMore && typeof HighchartsMore.default === 'function') {
    HighchartsMore.default(Highcharts);
}

export default function ChartBoxPlot({ title, dataDistribution, userValue, yAxisTitle }) {
    

    const themeColors = {
        line: '#00ff9d', 
        fill: 'rgba(0, 255, 157, 0.1)', 
        text: '#c0c0c0',
        userPoint: '#f1c40f'
    };

    const options = {
        chart: {
            type: 'boxplot',
            backgroundColor: 'transparent',
            height: 300,
            style: { fontFamily: "'Courier Prime', monospace" }
        },
        title: {
            text: title,
            align: 'left',
            style: { color: '#fff', fontSize: '16px', textTransform: 'uppercase' }
        },
        legend: { enabled: false },
        xAxis: {
            categories: ['Global'],
            lineWidth: 0,
            tickWidth: 0,
            labels: { enabled: false }
        },
        yAxis: {
            title: { text: yAxisTitle, style: { color: themeColors.text } },
            gridLineColor: '#235e6f',
            gridLineDashStyle: 'Dot',
            labels: { style: { color: themeColors.text } }
        },
        plotOptions: {
            boxplot: {
                fillColor: themeColors.fill,
                lineWidth: 2,
                medianColor: '#fff',
                medianWidth: 3,
                stemColor: themeColors.line,
                stemDashStyle: 'Solid',
                stemWidth: 1,
                whiskerColor: themeColors.line,
                whiskerLength: '20%',
                whiskerWidth: 2,
            }
        },
        series: [
            {
                name: 'Distribución Global',
                data: [dataDistribution],
                tooltip: {
                    headerFormat: '<em>Distribución Global</em><br/>',
                    pointFormat: 'Máximo: {point.high}<br/>Q3: {point.q3}<br/>Mediana: {point.median}<br/>Q1: {point.q1}<br/>Mínimo: {point.low}'
                }
            },
            {
                name: 'Tu Rendimiento',
                type: 'scatter',
                data: [[0, userValue]],
                color: themeColors.userPoint,
                marker: {
                    fillColor: themeColors.userPoint,
                    lineWidth: 2,
                    lineColor: 'rgba(255,255,255,0.5)',
                    radius: 6,
                    symbol: 'diamond'
                },
                tooltip: {
                    pointFormat: '<b>Tú: {point.y}</b>'
                }
            }
        ],
        credits: { enabled: false }
    };

    return <HighchartsReact highcharts={Highcharts} options={options} />;
}