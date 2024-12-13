FROM node:18 AS build

WORKDIR /app

COPY package*.json ./

RUN npm install


COPY . .

RUN npm run build

COPY ./build /build