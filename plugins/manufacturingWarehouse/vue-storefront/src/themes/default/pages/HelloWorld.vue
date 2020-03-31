<template>
  <div class="home">
    <h1>Home</h1>
    <button @click="info">
      Press me
    </button>
    <p>{{ msg }}</p>
  </div>
</template>
<script>
import api from 'theme/backend-api';
export default {
  name: 'TestHome',
  data () {
    return {
      msg: 'Welcome to Your Vue.js App'
    }
  },
  methods: {
    info () {
      $.get('http://localhost:4567/api/workefforts', function(data, status){
        // alert("Data: " + data + "\nStatus: " + status);
        console.log(data)
      });
      /*
      this.msg = 'Hello there!';
      api.getWorkEfforts().then(response => console.log(response)).then(data => console.log(data))
        .catch(error => {
          console.log('Error: ' + error);
          this.errors = error;
        }); */
    }
  }
}
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
