import React from 'react';
import '../App.css';
import '../static/css/home/home.css'; 
import logo from '../static/images/petris3D_recortado.png'

export default function Home(){
    return(
        <div className="home-page-container">
            <div className="hero-div">
                <h1>Petris</h1>
                <img src={logo} width={255} height={369} alt=""/>
                <h3>Do you want to play?</h3>                
            </div>
        </div>
    );
}