import gulp, {series, task} from 'gulp';
import rename from 'gulp-rename';
import cleanCSS from 'gulp-clean-css';
import gulpSass from 'gulp-sass';
import * as dartSass from 'sass';
import browserSyncModule from 'browser-sync';
import type {Transform} from 'stream';
import gulpTypescript from 'gulp-typescript';
import terser from 'gulp-terser';

const sass = gulpSass(dartSass);
const browserSync = browserSyncModule.create();

const tsProject = gulpTypescript.createProject('tsconfig.json');

function compileStylesheets() {
  return gulp.src('src/main/resources/sass/app.scss')
  .pipe(sass.sync({
        // Set to project root and node_modules
        // on purpose to make moj stylesheet find gov uk references
        // properly within node_modules.
        loadPaths: ['.', 'node_modules'],
        quietDeps: true,        // silences the @import deprecation noise from vendor files
        silenceDeprecations: ['import']
      })
      .on('error', function (this: Transform, err: Error) {
        // @ts-ignore: Usually shouldn't suppress, however this is a dev file and not for production.
        sass.logError.call(this, err);   // print the readable Sass error
        this.emit('end');                // end the stream so Gulp doesn't hang
      })
  )
  .pipe(cleanCSS())
  .pipe(rename('app.min.css'))
  .pipe(gulp.dest('src/main/resources/static/css'))
  .pipe(gulp.dest('build/resources/main/static/css'))
  .on('end', () => console.log('CSS written to build/resources/main/static/css'));
}

function copyGOVUKJavascript() {
  return gulp.src('node_modules/govuk-frontend/dist/govuk/govuk-frontend.min.js')
  .pipe(gulp.dest('src/main/resources/static/js'))
  .pipe(gulp.dest('build/resources/main/static/js'))
  .pipe(gulp.dest('src/main/resources/es'))
  .on('end', () => console.log('GOV.UK JS copied to build/resources/main/static/js and src/main/resources/es'));
}

function copyGOVUKAutocompleteJavascript() {
  return gulp.src('node_modules/accessible-autocomplete/dist/accessible-autocomplete.min.js')
  .pipe(gulp.dest('src/main/resources/static/js'))
  .pipe(gulp.dest('build/resources/main/static/js'))
  .pipe(gulp.dest('src/main/resources/es'))
  .on('end', () => console.log('GOV.UK Autocomplete JS copied to build/resources/main/static/js and src/main/resources/es'));

}

function copyMOJJavascript() {
  return gulp.src('node_modules/@ministryofjustice/frontend/moj/moj-frontend.min.js')
  .pipe(gulp.dest('build/resources/main/static/js'))
  .pipe(gulp.dest('src/main/resources/js'))
  .pipe(gulp.dest('src/main/resources/es'))
  .on('end', () => console.log('MOJ JS copied to build/resources/main/static/js and src/main/resources/es'));
}

function copyGOVUKAssets() {
  return gulp.src('node_modules/govuk-frontend/dist/govuk/assets/**/*')
  .pipe(gulp.dest('src/main/resources/static/assets'))
  .pipe(gulp.dest('build/resources/main/static/assets'))
  .on('end', () => console.log('GOV.UK assets copied to build/resources/main/static/assets'));
}

function copyMOJAssets() {
  return gulp.src('node_modules/@ministryofjustice/frontend/moj/assets/**/*')
  .pipe(gulp.dest('src/main/resources/static/assets'))
  .pipe(gulp.dest('build/resources/main/static/assets'))
  .on('end', () => console.log('MOJ assets copied to build/resources/main/static/assets'));
}

function compileScripts() {
  return gulp.src([
    'src/main/resources/es/**/*.ts',
    'src/main/resources/es/**/*.d.ts'
  ])
  .pipe(tsProject())
  .pipe(terser())
  .pipe(rename('app.min.js'))
  .pipe(gulp.dest('build/resources/main/static/js'))
  .pipe(gulp.dest('src/main/resources/static/js'))
  .on('end', () => console.log('App JS compiled to build/resources/main/static/js'));
}

function watch() {
  browserSync.init({
    proxy: {
      target: 'localhost:8090',
      proxyReq: [
        function (proxyReq: any) {
          // ask the backend not to compress, so BrowserSync can inject the snippet
          proxyReq.setHeader('Accept-Encoding', 'identity');
        }
      ],
      proxyRes: [
        function (proxyRes: any) {
          // strip CSP so BrowserSync's inline client script is allowed (DEV ONLY)
          delete proxyRes.headers['content-security-policy'];
          delete proxyRes.headers['content-security-policy-report-only'];
        }
      ]
    },
    open: true,
    notify: false
  });

  gulp.watch([
    'src/main/resources/templates/**',
    'src/main/resources/templates/**/*'
  ])
  .on('change', (path) => {
    console.log('Template changed:', path);
    browserSync.reload();
  });

  gulp.watch('src/main/resources/sass/**/*.scss', series(compileStylesheets))
  .on('change', (path) => {
    console.log('Stylesheet changed:', path);
    browserSync.reload();
  });

  gulp.watch('src/main/resources/es/**/*.{ts,js}', series(compileScripts))
  .on('change', (path) => {
    console.log('Script changed:', path);
    browserSync.reload();
  });

}

task('copy-assets', series(copyGOVUKAssets, copyMOJAssets));
task('copy-js', series(copyGOVUKJavascript, copyGOVUKAutocompleteJavascript, copyMOJJavascript));
task('compile-stylesheets', compileStylesheets);
task('compile-scripts', series('copy-js', compileScripts));

task('default', series('copy-assets', 'compile-stylesheets', 'compile-scripts'));
task('watch', series(watch));
