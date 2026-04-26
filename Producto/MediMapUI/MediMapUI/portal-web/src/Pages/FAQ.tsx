import { useState } from 'react';
import './FAQ.css';

const FAQ = () => {
  const [preguntaActiva, setPreguntaActiva] = useState<number | null>(null);

  const togglePregunta = (index: number) => {
    setPreguntaActiva(preguntaActiva === index ? null : index);
  };

  // 🔥 Añadimos un icono a cada pregunta para hacer el diseño más rico
  const listaPreguntas = [
    {
      icono: "📍",
      pregunta: "¿Qué es MediMapa?",
      respuesta: "MediMapa es una plataforma web informativa y geolocalizada enfocada en la comuna de La Florida. Te permitimos buscar medicamentos, comparar precios referenciales entre distintas cadenas y descubrir alternativas bioequivalentes certificadas para cuidar tu salud y tu bolsillo."
    },
    {
      icono: "🛒",
      pregunta: "¿Puedo comprar los medicamentos directamente en esta página?",
      respuesta: "No. MediMapa opera bajo un modelo de agregador informativo y no procesa pagos ni ventas directas. Nuestra función es transparentar los precios para facilitar tu decisión de compra, derivándote a la página oficial de la farmacia o indicándote su ubicación física."
    },
    {
      icono: "🔒",
      pregunta: "¿Necesito registrarme para buscar un medicamento?",
      respuesta: "Absolutamente no. Protegemos tu privacidad operando bajo la Ley 21.719. Todo tu procesamiento de búsqueda es volátil, lo que significa que no guardamos historiales médicos, direcciones exactas ni requerimos que inicies sesión para cotizar."
    },
    {
      icono: "B", // O podrías usar un SVG de tu sello aquí
      pregunta: "¿Qué significa que un medicamento sea Bioequivalente?",
      respuesta: "Son medicamentos que han demostrado ante el Instituto de Salud Pública (ISP) que contienen el mismo principio activo y tienen la misma eficacia clínica que el medicamento original de marca, pero generalmente a un precio mucho más económico."
    },
    {
      icono: "🏪",
      pregunta: "Tengo una farmacia independiente, ¿puedo aparecer en el mapa?",
      respuesta: "¡Sí! Hemos desarrollado un Módulo de Inclusión Comercial para democratizar la vitrina digital. Puedes registrar tu local en la sección 'Inscribe tu Farmacia', donde te pediremos datos profesionales básicos para validar la legalidad del establecimiento y habilitar tu catálogo."
    }
  ];

  return (
    <div className="faq-container">
      
      {/* 🔥 NUEVO BANNER SUPERIOR 🔥 */}
      <div className="faq-banner">
        <h2>Centro de Ayuda</h2>
        <p>Resolvemos tus dudas sobre cómo funciona nuestro buscador de medicamentos.</p>
      </div>

      <div className="faq-accordion">
        {listaPreguntas.map((item, index) => (
          <div 
            key={index} 
            className={`faq-item ${preguntaActiva === index ? 'activo' : ''}`}
          >
            <button 
              className="faq-question" 
              onClick={() => togglePregunta(index)}
            >
              <div className="faq-question-title">
                <span className="faq-question-icon">{item.icono}</span>
                <span>{item.pregunta}</span>
              </div>
              <span className="faq-icon">
                {preguntaActiva === index ? '−' : '+'}
              </span>
            </button>
            
            <div className="faq-answer-wrapper">
              <div className="faq-answer">
                <p>{item.respuesta}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default FAQ;