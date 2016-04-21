/**
 * Created by Arnoldas on 2016-04-17.
 */

function myFunction1() {
       if(sessionStorage.getItem("lastname1")==0)
    {
        var body = document.getElementsByTagName('body')[0];
        body.style.backgroundImage = 'url(/resources/cloudgreen.png)';
    }else {
        var body = document.getElementsByTagName('body')[0];
        body.style.backgroundImage = 'url(/resources/cloud.png)';
    }
}


