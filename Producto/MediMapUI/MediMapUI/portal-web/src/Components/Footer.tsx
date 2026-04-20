
import { Link } from 'react-router-dom';
import './Footer.css';

const Footer = () => {
  const anioActual = new Date().getFullYear();

  return (
    <footer className="modern-footer">
      <div className="footer-grid">
        
        {/* Columna 1: Marca */}
        <div className="footer-brand">
          <h3>MediMapa 🏥</h3>
          <p>
            Tu buscador inteligente de medicamentos. Comparamos los inventarios 
            en tiempo real para que cuides tu salud y tu bolsillo en la comuna de La Florida.
          </p>
        </div>

        {/* Columna 2: Enlaces */}
        <div className="footer-links">
          <h4>Enlaces Rápidos</h4>
          <ul>
            <li><Link to="/">🔍 Buscador Principal</Link></li>
            <li><Link to="/catalogo">📘 Catálogo ISP</Link></li>
            <li><Link to="/red-farmacias">📍 Mapa de Farmacias</Link></li>
            <li><Link to="/faq">🩺 Preguntas Frecuentes</Link></li>
            <li><Link to="/login">⚙️ Acceso Administradores</Link></li>
          </ul>
        </div>

        {/* Columna 3: Legal y Advertencias */}
        <div className="footer-legal">
          <h4>Transparencia y Uso</h4>
          <p>⚠️ <strong>Aviso Médico:</strong> MediMapa es una herramienta estrictamente informativa. No prescribimos ni vendemos medicamentos.</p>
          <p>📊 <strong>Precios:</strong> Los valores mostrados son referenciales y dependen de la disponibilidad física en cada sucursal al momento de la compra.</p>
          <p>💊 Consulte siempre a su médico antes de automedicarse.</p>
        </div>
        
      </div>

      <div className="footer-bottom">
        <p>© {anioActual} MediMapa. Todos los derechos reservados.</p>
        <p className="highlight-duoc">Proyecto de Integración - Duoc UC</p>
      </div>
    </footer>
  );
};

export default Footer;