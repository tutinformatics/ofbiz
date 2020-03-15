<template>
  <div class="home">
    <FormBuilder v-bind:form="form" />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { FormBuilder } from 'vue-formio';

@Component({
  components: {
    FormBuilder,
  },
  data: () => {
    return {
      form: {},
    };
  },
})
export default class Home extends Vue {}
</script>
