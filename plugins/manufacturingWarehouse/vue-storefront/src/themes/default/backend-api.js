import axios from 'axios';

const AXIOS = axios.create({
  baseURL: 'http://localhost:4567/api',
  timeout: 5000,
  headers: {
    'Content-Type': 'text',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'DELETE, POST, GET, OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With'
  }
});

export default {
  getWorkEfforts () {
    // return $.get()
    /* fetch(
      'http://localhost:4567/api/workefforts',
      {
        method: 'GET',
        mode: 'cors',
        redirect: 'follow',
        headers: {
          'Accept': 'application/json',
          'Access-Control-Allow-Origin': '*',
          'Content-Type': 'application/json'
        }
      }
    ); */

    /*
    fetch(
      'http://localhost:4567/api/workefforts',
      {method: 'GET'}
    ); */
    /*
    return AXIOS.get(
      '/workefforts'
    ); */
  }
};
