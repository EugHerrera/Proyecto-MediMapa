# Etapa 1: Construcción (¡Actualizado a Node 20!)
FROM node:20-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

# Como ya arreglaste el TypeScript, podemos usar el comando original
RUN npm run build 

# Etapa 2: Servidor Web (Nginx)
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]