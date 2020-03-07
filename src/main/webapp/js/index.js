function getAllPersons(){
    fetch('api/Person/all')
    .then(res=> res.json())
    .then(data => populateTable(data));

}

function addPerson(){
const fNameAdd = document.getElementById('fNameAdd').value;
const lNameAdd = document.getElementById('lNameAdd').value;
const phoneAdd = document.getElementById('phoneAdd').value;

let options = {
   method: "POST",
   headers: {
   'Accept': 'application/json',
   'Content-Type': 'application/json'
   },
   body: JSON.stringify({
     fName: fNameAdd,
     lName: lNameAdd,
     phone: phoneAdd

   })
}

fetch("api/Person/add",options);
document.getElementById('fNameAdd').value = "";
document.getElementById('lNameAdd').value = "";
document.getElementById('phoneAdd').value = "";

getAllPersons();
}

const populateTable = data => {
    const dataArray = data.all.map( data => `<tr><td>${data.id}</td><td>${data.fName}</td><td>${data.lName}</td><td>${data.phone}</td></tr>`)
    document.getElementById('tableBody').innerHTML = dataArray.join('');
}

window.addEventListener('DOMContentLoaded', getAllPersons);
document.getElementById('reloadBtn').addEventListener('click', getAllPersons);
document.getElementById('addPerson').addEventListener('click', addPerson)