# Etapa 1: Construcción 
FROM node:20-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

# Como ya arreglaste el TypeScript, podemos usar el comando original
RUN npm run build 

# Etapa 2: Servidor Web (Nginx)
# ETAPA DE PRODUCCIÓN CON NGINX
FROM nginx:alpine
# Copiamos la build de Vite
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]