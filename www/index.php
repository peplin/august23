<?php
require_once("include/config.php");
require_once("connections/august.php");
include("include/$topfile");

$stars = mysql_query("SELECT * FROM object NATURAL JOIN star LEFT JOIN (user) ON (object.owner = user.id)", $august);
echo "<div><ul>";
while ($star = mysql_fetch_row($stars)) {
    echo "<li><ul>";
    for ($i = 0; $i < count($star); $i++){
        echo "<li>";
        htmlspecialchars($star[$i]);
        echo "</li>";
    }
    echo "</ul></li>";
}
?>
