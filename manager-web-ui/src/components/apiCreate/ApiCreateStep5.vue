<template>
  <div>
    <highlightjs language="json" :code="apiDtoJsonString" />
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

defineProps({
  apiDtoJsonString: String
})
</script>
