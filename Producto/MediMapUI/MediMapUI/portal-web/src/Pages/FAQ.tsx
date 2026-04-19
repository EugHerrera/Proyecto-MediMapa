import React, { useState } from 'react';
import './FAQ.css';

const FAQ = () => {
  // Estado para saber qué pregunta está abierta. null significa que todas están cerradas.
  const [preguntaActiva, setPreguntaActiva] = useState<number | null>(null);

  const togglePregunta = (index: number) => {
    // Si haces clic en la que ya está abierta, se cierra. Si no, se abre la nueva.
    setPreguntaActiva(preguntaActiva === index ? null : index);
  };

  // Preguntas basadas en la arquitectura y alcance de MediMapa
  const listaPreguntas = [
    {
      pregunta: "¿Qué es MediMapa?",
      respuesta: "MediMapa es una plataforma web informativa y geolocalizada enfocada en la comuna de La Florida. Te permitimos buscar medicamentos, comparar precios referenciales entre distintas cadenas y descubrir alternativas bioequivalentes certificadas para cuidar tu salud y tu bolsillo."
    },
    {
      pregunta: "¿Puedo comprar los medicamentos directamente en esta página?",
      respuesta: "No. MediMapa opera bajo un modelo de agregador informativo y no procesa pagos ni ventas directas[cite: 126, 127]. Nuestra función es transparentar los precios para facilitar tu decisión de compra, derivándote a la página oficial de la farmacia o indicándote su ubicación física[cite: 128]."
    },
    {
      pregunta: "¿Necesito registrarme para buscar un medicamento?",
      respuesta: "Absolutamente no. Protegemos tu privacidad operando bajo la Ley 21.719[cite: 129]. Todo tu procesamiento de búsqueda es volátil, lo que significa que no guardamos historiales médicos, direcciones exactas ni requerimos que inicies sesión para cotizar[cite: 130]."
    },
    {
      pregunta: "¿Qué significa que un medicamento sea Bioequivalente?",
      respuesta: "Son medicamentos que han demostrado ante el Instituto de Salud Pública (ISP) que contienen el mismo principio activo y tienen la misma eficacia clínica que el medicamento original de marca, pero generalmente a un precio mucho más económico."
    },
    {
      pregunta: "Tengo una farmacia independiente, ¿puedo aparecer en el mapa?",
      respuesta: "¡Sí! Hemos desarrollado un Módulo de Inclusión Comercial para democratizar la vitrina digital. Puedes registrar tu local en la sección 'Inscribe tu Farmacia', donde te pediremos datos profesionales básicos para validar la legalidad del establecimiento y habilitar tu catálogo."
    }
  ];

  return (
    <div className="faq-container">
      <div className="faq-header">
        <h2>Preguntas Frecuentes</h2>
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
              {item.pregunta}
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