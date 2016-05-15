/**
 * Created by Arnoldas on 2016-04-17.
 */

function reloader() {
    var body = document.getElementsByTagName('body')[0];
    if(sessionStorage.getItem("backgroundIndex") == 0) {
        body.style.backgroundImage = 'url(/resources/cloudgreen.png)';
        body.style.color = '#ccccff';
    }
    else if(sessionStorage.getItem("backgroundIndex") == 1) {
        body.style.backgroundImage = 'url(/resources/nightcloud.jpg)';
        body.style.color = '#ccccff';
    }
    else if(sessionStorage.getItem("backgroundIndex") == 2) {
        body.style.backgroundImage = 'url(/resources/stormcloud.jpg)';
        body.style.color = '#666699';
    }
    else {
        body.style.backgroundImage = 'url(/resources/cloud.png)';
    }

    if(sessionStorage.getItem("fontSize") == 14) {
        body.style.fontSize = '14pt';
    }
    else if(sessionStorage.getItem("fontSize") == 16) {
        body.style.fontSize = '16pt';
    }
    else if(sessionStorage.getItem("fontSize") == 18) {
        body.style.fontSize = '18pt';
    }
    else if(sessionStorage.getItem("fontSize") == 20) {
        body.style.fontSize = '20pt';
    }
    else if(sessionStorage.getItem("fontSize") == 22) {
        body.style.fontSize = '22pt';
    }
    else if(sessionStorage.getItem("fontSize") == 24) {
        body.style.fontSize = '24pt';
    }
    else {
        body.style.fontSize = '12pt';
    }
}

