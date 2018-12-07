(function () {
    var debugMode, touchMode, locale, localeParameter, extjsVersion, proj4jsVersion, fontAwesomeVersion, olVersion, i, language, languages, languageDefault;

    function addStyleFile(file) {
        var link = document.createElement('link');
        link.setAttribute('rel', 'stylesheet');
        link.setAttribute('type', 'text/css');
        link.setAttribute('href', file);
        document.head.appendChild(link);
    }

    function addScriptFile(file) {
        var script = document.createElement('script');
        script.setAttribute('src', file);
        script.async = false;
        document.head.appendChild(script);
    }

    function addSvgFile(file, id) {
        var svg = document.createElement('object');
        svg.setAttribute('id', id);
        svg.setAttribute('data', file);
        svg.setAttribute('type', 'image/svg+xml');
        svg.setAttribute('style', 'visibility:hidden;position:absolute;left:-100px;');
        document.body.appendChild(svg);
    }

    debugMode = document.getElementById('loadScript').getAttribute('mode') === 'debug';
    touchMode = 'ontouchstart' in window || navigator.maxTouchPoints;

    locale = {};
    window.Locale = locale;

    locale.languages = {
        'en': { name: 'English', code: 'en' },
		'mn': { name: 'Монгол', code: 'mn' }
    };

    languageDefault = 'mn';
    localeParameter = window.location.search.match(/locale=([^&#]+)/);
    locale.language = localeParameter && localeParameter[1];
    if (!(locale.language in locale.languages)) {
        languages = window.navigator.languages !== undefined ? window.navigator.languages.slice() : [];
        language = window.navigator.userLanguage || window.navigator.language;
        languages.push(language);
        languages.push(languageDefault);
        languages.push(language.substr(0, 2));
//        for (i = 0; i < languages.length; i++) {
//            language = languages[i].replace('-', '_');
//            if (language in locale.languages) {
//                locale.language = language;
//                break;
//            }
//        }
        locale.language = languageDefault;
    }

    window.addEventListener('load', function (event) {

        if (debugMode) {
            Ext.Loader.setConfig({
                disableCaching: false
            });
        }

        Ext.Ajax.request({
            url: 'languages/' + languageDefault + '.json',
            callback: function (options, success, response) {
                window.Strings = Ext.decode(response.responseText);
                if (Locale.language !== languageDefault) {
                    Ext.Ajax.request({
                        url: 'languages/' + Locale.language + '.json',
                        callback: function (options, success, response) {
                            var key, data = Ext.decode(response.responseText);
                            for (key in data) {
                                if (data.hasOwnProperty(key)) {
                                    window.Strings[key] = data[key];
                                }
                            }
                            addScriptFile(debugMode ? 'app.js' : 'app.min.js');
                        }
                    });
                } else {
                    addScriptFile(debugMode ? 'app.js' : 'app.min.js');
                }
            }
        });

    });

    extjsVersion = '6.2.0';
    fontAwesomeVersion = '4.7.0';
    olVersion = '4.3.2';
    proj4jsVersion = '2.4.3';

//	console.log(debugMode);

    if (debugMode) {
        addScriptFile('//cdnjs.cloudflare.com/ajax/libs/extjs/' + extjsVersion + '/ext-all-debug.js');
        addScriptFile('//cdnjs.cloudflare.com/ajax/libs/extjs/' + extjsVersion + '/packages/charts/classic/charts-debug.js');
    } else {
        addScriptFile('//cdnjs.cloudflare.com/ajax/libs/extjs/' + extjsVersion + '/ext-all.js');
        addScriptFile('//cdnjs.cloudflare.com/ajax/libs/extjs/' + extjsVersion + '/packages/charts/classic/charts.js');
    }
    addScriptFile('//cdnjs.cloudflare.com/ajax/libs/extjs/' + extjsVersion + '/classic/locale/locale-' + locale.languages[locale.language].code + '.js');

    addStyleFile('//cdnjs.cloudflare.com/ajax/libs/extjs/' + extjsVersion + '/classic/theme-crisp-touch/resources/theme-crisp-touch-all.css');
    addScriptFile('//cdnjs.cloudflare.com/ajax/libs/extjs/' + extjsVersion + '/classic/theme-crisp-touch/theme-crisp-touch.js');
    
    addScriptFile('//cdnjs.cloudflare.com/ajax/libs/FileSaver.js/1.3.8/FileSaver.min.js')
    
    addScriptFile('//cdnjs.cloudflare.com/ajax/libs/jszip/3.1.5/jszip.min.js')
    
    addStyleFile('//cdnjs.cloudflare.com/ajax/libs/extjs/' + extjsVersion + '/packages/charts/classic/crisp/resources/charts-all.css');

    addStyleFile('//cdnjs.cloudflare.com/ajax/libs/font-awesome/' + fontAwesomeVersion + '/css/font-awesome.min.css');

    addStyleFile('//cdnjs.cloudflare.com/ajax/libs/ol3/' + olVersion + '/ol.css');
    if (debugMode) {
        addScriptFile('//cdnjs.cloudflare.com/ajax/libs/ol3/' + olVersion + '/ol-debug.js');
    } else {
        addScriptFile('//cdnjs.cloudflare.com/ajax/libs/ol3/' + olVersion + '/ol.js');
    }

    if (debugMode) {
        addScriptFile('//cdnjs.cloudflare.com/ajax/libs/proj4js/' + proj4jsVersion + '/proj4-src.js');
    } else {
        addScriptFile('//cdnjs.cloudflare.com/ajax/libs/proj4js/' + proj4jsVersion + '/proj4.js');
    }

    window.Images = ['arrow', 'default', 'animal', 'bicycle', 'boat', 'bus', 'car', 'crane', 'helicopter',
        'motorcycle', 'offroad', 'person', 'pickup', 'plane', 'ship', 'tractor', 'truck', 'van'];

    for (i = 0; i < window.Images.length; i++) {
        addSvgFile('images/' + window.Images[i] + '.svg', window.Images[i] + 'Svg');
    }
})();
