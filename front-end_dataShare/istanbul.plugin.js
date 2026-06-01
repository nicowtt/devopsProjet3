import { createInstrumenter } from 'istanbul-lib-instrument';
import { readFileSync } from 'fs';

const instrumenter = createInstrumenter({
  esModules: true,
  compact: false,
  produceSourceMap: true,
  coverageVariable: '__coverage__',
});

/** @type {import('esbuild').Plugin} */
export default {
  name: 'istanbul',
  setup(build) {
    build.onLoad({ filter: /\.ts$/ }, async (args) => {
      if (args.path.includes('node_modules')) return null;
      const source = readFileSync(args.path, 'utf8');
      const instrumented = instrumenter.instrumentSync(source, args.path);
      return { contents: instrumented, loader: 'ts' };
    });
  },
};
