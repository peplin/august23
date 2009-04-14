<?php
require_once("include/config.php");
require_once("connections/august.php");
include("include/$topfile");

$stars = mysql_query(
    "SELECT birth, colorR, colorG, colorB, frequency, state.name
        FROM object NATURAL JOIN star 
        LEFT JOIN (user, state)
            ON (object.owner = user.id AND star.state = state.id) 
        ORDER BY birth DESC", $august);
echo "<div><table id=\"star_table\" class=\"center\">";
echo "<thead>";
echo "<tr>
        <th scope=\"col\" id=\"birth_col\">Born</th>
        <th scope=\"col\" id=\"color_r_col\">Color (Red)</th>
        <th scope=\"col\" id=\"color_g_col\">Color (Green)</th>
        <th scope=\"col\" id=\"color_b_col\">Color (Blue)</th>
        <th scope=\"col\" id=\"frequency_col\">Frequency</th>
        <th scope=\"col\" id=\"state_col\">State</th>
     </tr>";
echo "</thead><tbody>";
$row = 0;
while ($star = mysql_fetch_row($stars)) {
    echo "<tr class=\"row".($row & 1)."\">\n";
    for ($i = 0; $i < count($star); $i++){
        echo "\t<td>";
        echo $star[$i];
        echo "</td>\n";
    }
    echo "</tr>\n";
    $row++;
}
echo "</tbody></table>";

echo "<div id=\"menu\"><ul class=\"center\">";
echo "<li><a href=\"http://august231966.com\">August 23, 1966</a></li>";
echo "<li><a href=\"http://www.dc.umich.edu/grocs/\">GROCS</a></ul></div>";
include("include/$botfile");
?>
