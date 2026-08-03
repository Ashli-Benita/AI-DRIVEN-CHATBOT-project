import { useEffect, useState } from 'react';
import { clearChat, getHistory, login, register, sendChat } from './auth';

export default function App() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [authMode, setAuthMode] = useState('login');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [authMessage, setAuthMessage] = useState('');
  const [isAuthenticated, setIsAuthenticated] = useState(Boolean(localStorage.getItem('token')));

  useEffect(() => {
    if (isAuthenticated) {
      loadHistory();
    }
  }, [isAuthenticated]);

  const loadHistory = async () => {
    try {
      const response = await getHistory();
      setMessages(response.data.map((item) => ({
        role: 'assistant',
        content: item.botReply,
        user: item.userMessage,
      })));
    } catch (error) {
      console.error('history load failed', error);
    }
  };

  const handleAuth = async (e) => {
    e.preventDefault();
    try {
      const result = authMode === 'login'
        ? await login(username, password)
        : await register(username, email, password);
      setIsAuthenticated(true);
      setAuthMessage(`Welcome, ${result.username}`);
    } catch (error) {
      setAuthMessage(error.response?.data?.message || 'Authentication failed');
    }
  };

  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim()) return;

    const userMessage = input.trim();
    setMessages((prev) => [...prev, { role: 'user', content: userMessage }]);
    setInput('');
    setLoading(true);

    try {
      const response = await sendChat(userMessage);
      setMessages((prev) => [...prev, { role: 'assistant', content: response.data || 'No response' }]);
    } catch (error) {
      console.error('send failed', error);
      setMessages((prev) => [...prev, { role: 'assistant', content: 'Unable to reach the backend.' }]);
    } finally {
      setLoading(false);
    }
  };

  const handleClear = async () => {
    try {
      await clearChat();
      setMessages([]);
    } catch (error) {
      console.error('clear failed', error);
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="app-shell">
        <h1>AI Chatbot</h1>
        <form className="auth-card" onSubmit={handleAuth}>
          <h2>{authMode === 'login' ? 'Login' : 'Register'}</h2>
          <input value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Username" />
          {authMode === 'register' ? <input value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" /> : null}
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" />
          <button type="submit">{authMode === 'login' ? 'Login' : 'Register'}</button>
          <button type="button" className="secondary" onClick={() => setAuthMode(authMode === 'login' ? 'register' : 'login')}>
            Switch to {authMode === 'login' ? 'Register' : 'Login'}
          </button>
          {authMessage ? <p>{authMessage}</p> : null}
        </form>
      </div>
    );
  }

  return (
    <div className="app-shell">
      <h1>AI Chatbot</h1>
      {authMessage ? <p>{authMessage}</p> : null}
      <div className="messages">
        {messages.map((message, index) => (
          <div key={index} className={`message ${message.role}`}>
            {message.user ? <strong>You:</strong> : null} {message.user || message.content}
            {!message.user ? <div>{message.content}</div> : null}
          </div>
        ))}
      </div>
      <form onSubmit={handleSend} className="composer">
        <input value={input} onChange={(e) => setInput(e.target.value)} placeholder="Type your message" />
        <button type="submit" disabled={loading}>{loading ? 'Sending...' : 'Send'}</button>
      </form>
      <button className="secondary" onClick={handleClear}>Clear History</button>
    </div>
  );
}
