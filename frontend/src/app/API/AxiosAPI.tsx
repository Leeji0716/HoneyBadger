import axios from 'axios';

export function getAPI() {
    const api = axios.create({
        // baseURL: 'http://localhost:8080',
        baseURL: 'http://www.벌꿀오소리.메인.한국:8080',
        headers: {
            'Content-Type': 'application/json;charset=utf-8;',
        },
    });
    return api;
}