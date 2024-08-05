<script lang="ts">
  import { TextElement } from './model';
  import type { PrintElement } from './model';

  const elements: Array<PrintElement> = [new TextElement(0, 'Hello'), new TextElement(1, 'World')];
</script>

<main>
  <div class="wrapper">
    {#each elements as element}
      {#if element instanceof TextElement}
        <p>{element.text}</p>
      {/if}
    {/each}
  </div>
</main>

<style>
  .wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
  }
</style>
