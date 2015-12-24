<?php
$hostsFile = "C:/Windows/System32/drivers/etc/hosts";
echo $hostsFile;
$RegExp = '/#google hosts [0-9]+[\s\S]+#google hosts [0-9]+ end/';
 
$html = strip_tags(file_get_contents('http://www.360kb.com/kb/2_122.html'));
//echo $html
preg_match($RegExp, $html, $matchs);
print_r($matchs);
echo "<br/>";
$googleHosts = str_replace('&nbsp;', ' ', $matchs[0]);
echo $googleHosts;
 
$hosts = file_get_contents($hostsFile);
 
if(preg_match($RegExp, $hosts)){
    $hosts = preg_replace($RegExp, $googleHosts, $hosts);
}else{
    $hosts .= "\r\n\r\n".$googleHosts."\r\n\r\n";
}
 
file_put_contents($hostsFile, $hosts);
 

?>