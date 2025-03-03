import { createRouter, createWebHistory } from 'vue-router';
import { useStore } from 'vuex';

// Import your components/views
import Events from '../components/Events.vue';
import DJPage from '../views/DJPage.vue'; // New DJ page
import HomeView from '../views/HomeView.vue';
import HostPage from '../views/HostPage.vue'; // New Host page
import LandingPageView from '../views/LandingPageView.vue';
import LoginView from '../views/LoginView.vue';
import LogoutView from '../views/LogoutView.vue';
import RegisterView from '../views/RegisterView.vue';

//spotify
import Playlists from '../views/Playlists.vue';
import PlaylistEditor from '../views/PlaylistEditor.vue';
import SpotifyLogin from '../views/SpotifyLogin.vue';
import SpotifyCallback from '../views/SpotifyCallback.vue';
import SpotifyTestComponent from '../components/SpotifyTest.vue';

const routes = [
  {
    path: '/spotify',
    name: 'spotify',
    component: SpotifyTestComponent,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'landing',
    component: LandingPageView,
    meta: { requiresAuth: false }
  },
  {
    path: '/home',
    name: 'home',
    component: HomeView,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { requiresAuth: false }
  },
  {
    path: '/logout',
    name: 'logout',
    component: LogoutView,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: { requiresAuth: false }
  },
  {
    path: '/events',
    name: 'events',
    component: Events,
    meta: { requiresAuth: true }
  },
  // New routes for DJ and Host pages
  {
    path: '/dj',
    name: 'djPage',
    component: DJPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/host',
    name: 'hostPage',
    component: HostPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/playlists',
    name: 'playlists',
    component: Playlists,
    meta: { requiresAuth: true }
  },
  {
    path: '/playlist-editor',
    name: 'playlistEditor',
    component: PlaylistEditor,
    meta: { requiresAuth: true }
  },
  {
    path: '/spotify-login',
    name: 'spotifyLogin',
    component: SpotifyLogin,
    meta: {requiresAuth: false}
  },
  {
    path: '/spotify-callback',
    name: 'spotifyCallback',
    component: SpotifyCallback,
    meta: {requiresAuth: false}
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// Global navigation guard to protect routes
router.beforeEach((to) => {
  const store = useStore();
  const requiresAuth = to.matched.some(x => x.meta.requiresAuth);
  if (requiresAuth && store.state.token === '') {
    return { name: 'login' };
  }
});

export default router;
