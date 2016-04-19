/**
 * Created by Arnoldas on 2016-04-17.
 */

function myFunction1() {
   // alert(localStorage.getItem("lastname"));
    //alert(sessionStorage.getItem("lastname1"));
   /* if(localStorage.getItem("lastname1")==0){
        alert("doublechanges");
    }*/

    if(sessionStorage.getItem("lastname1")==0)
    {
        //alert("changes");
        var body = document.getElementsByTagName('body')[0];
        body.style.backgroundImage = 'url(/resources/cloudgreen.png)';
    }else {
        var body = document.getElementsByTagName('body')[0];
        body.style.backgroundImage = 'url(/resources/cloud.png)';
    }
}


