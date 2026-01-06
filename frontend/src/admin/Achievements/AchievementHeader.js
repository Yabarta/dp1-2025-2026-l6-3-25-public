import React from "react";
import { Button } from "reactstrap";
import { FaTrophy } from "react-icons/fa";
import "../../static/css/admin/achievementListAdmin.css";

export default function AchievementHeader({ searchName, setSearchName, onCreateClick }) {
    return (
        <div className="achievement-header">
            <div className="achievement-title-section">
                <FaTrophy className="achievement-icon" />
                <h1 className="achievement-title">Gestionar Logros</h1>
            </div>
            <div className="achievement-header-controls">
                <input
                    type="text"
                    placeholder="Buscar logro..."
                    className="achievement-search"
                    value={searchName}
                    onChange={(e) => setSearchName(e.target.value)}
                />
                <Button 
                    color="success" 
                    onClick={onCreateClick}
                    className="achievement-create-btn"
                >
                    + Crear Logro
                </Button>
            </div>
        </div>
    );
}
