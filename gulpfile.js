const gulp = require('gulp');
const {task, series} = require("gulp");
const sass = require('gulp-sass')(require('sass'));
const rename = require('gulp-rename');
const cleanCSS = require('gulp-clean-css');
const browserSync = require('browser-sync').create();

function compileStylesheets() {
  return gulp.src('src/main/resources/sass/app.scss')
  .pipe(sass.sync({
    // Set to project root and node_modules
    // on purpose to make moj stylesheet find gov uk references
    // properly within node_modules.
    loadPaths: ['.','node_modules'],
    quietDeps: true,        // silences the @import deprecation noise from vendor files
    silenceDeprecations: ['import']
  })
    .on('error', function (err) {
        sass.logError.call(this, err);   // print the readable Sass error
        this.emit('end');                // end the stream so Gulp doesn't hang
    })
  )
  .pipe(cleanCSS())
  .pipe(rename('app.min.css'))
  .pipe(gulp.dest('src/main/resources/static/css'))
  .on('end', () => console.log('CSS written to src/main/resources/static/css'));
}

function copyGOVUKJavascript(){
  return gulp.src('node_modules/govuk-frontend/dist/govuk/govuk-frontend.min.js')
  .pipe(gulp.dest('src/main/resources/static/js'))
  .on('end', () => console.log('GOV.UK JS copied to src/main/resources/static/js'));
}

function copyMOJJavascript(){
  return gulp.src('node_modules/@ministryofjustice/frontend/moj/moj-frontend.min.js')
  .pipe(gulp.dest('src/main/resources/static/js'))
  .on('end', () => console.log('MOJ JS copied to src/main/resources/static/js'));
}

function copyGOVUKAssets(){
  return gulp.src('node_modules/govuk-frontend/dist/govuk/assets/**/*')
  .pipe(gulp.dest('src/main/resources/static/assets'))
  .on('end', () => console.log('GOV.UK assets copied to src/main/resources/static/assets'));
}

function copyMOJAssets(){
  return gulp.src('node_modules/@ministryofjustice/frontend/moj/assets/**/*')
  .pipe(gulp.dest('src/main/resources/static/assets'))
  .on('end', () => console.log('MOJ assets copied to src/main/resources/static/assets'));
}

function watch() {
  browserSync.init({
    proxy: {
      target: 'localhost:8090',
      proxyReq: [
        function (proxyReq) {
          // ask the backend not to compress, so BrowserSync can inject the snippet
          proxyReq.setHeader('Accept-Encoding', 'identity');
        }
      ],
      proxyRes: [
        function (proxyRes) {
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

  gulp.watch('src/main/resources/sass/**/*.scss')
  .on('change', (path) => {
    console.log('Stylesheet changed:', path);
    browserSync.reload();
  });

}

task('copy-assets', series(copyGOVUKAssets, copyMOJAssets));
task('copy-js', series(copyGOVUKJavascript, copyMOJJavascript));
task('compile-stylesheets', compileStylesheets);

task('default',  series('copy-assets', 'copy-js', 'compile-stylesheets'));
task('watch', series(watch));
