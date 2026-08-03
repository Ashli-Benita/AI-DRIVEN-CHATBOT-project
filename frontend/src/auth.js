import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  timeout: 20000,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const login = async (username, password) => {
  const response = await api.post('/api/auth/login', { username, password });
  if (response.data?.token) {
    localStorage.setItem('token', response.data.token);
  }
  return response.data;
};

export const register = async (username, email, password) => {
  const response = await api.post('/api/auth/register', { username, email, password });
  if (response.data?.token) {
    localStorage.setItem('token', response.data.token);
  }
  return response.data;
};

export const getHistory = () => api.get('/chat/history');
export const sendChat = (message) => api.get('/chat', { params: { message } });
export const clearChat = () => api.delete('/chat/clear');

export default api;
