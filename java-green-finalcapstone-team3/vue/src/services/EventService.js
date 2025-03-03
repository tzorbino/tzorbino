import axios from 'axios';

const http = axios.create({
    baseURL: import.meta.env.VITE_REMOTE_API
  });

export default {
    getEvents() {
        return http.get('/events');
    },

    getEventById(eventId) {
        return axios.get(`${API_URL}/${eventId}`);
    },

    createEvent(eventData) {
        return axios.post(API_URL, eventData);
    }
}
