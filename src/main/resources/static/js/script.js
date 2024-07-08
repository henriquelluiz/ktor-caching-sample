const nameField = document.getElementById("nameField")
const noteField = document.getElementById("noteField")

function addTask() {
    if (nameField && noteField) {
        let name = nameField.value
        let note = noteField.value

        fetch('/api/tasks', {
            headers: {
                'Accept': "application/json",
                'Content-type': "application/json"
            },
            method: "POST",
            body: JSON.stringify({
                name: name, note: note
            })
        })
            .then(res => res.text())
            .then(data => {
                console.log(data)
                navigateTo("/")
            })
    }
}

function editTask(id) {
    if (nameField && noteField) {
        let name = nameField.value
        let note = noteField.value

        fetch(`/api/tasks/edit?id=${id}`, {
            headers: {
                'Accept': "application/json",
                'Content-type': "application/json"
            },
            method: "PUT",
            body: JSON.stringify({
                name: name, note: note
            })
        })
            .then(res => res.text())
            .then(data => {
                console.log(data)
                navigateTo("/")
            })
    }
}

function deleteTask(id) {
    fetch(`/api/tasks/delete?id=${id}`, {
        headers: {
            'Accept': "application/json",
            'Content-type': "application/json"
        },
        method: "DELETE"
    })
        .then(res => res.text())
        .then(data => {
            console.log(data)
            navigateTo("/")
        })
}

function navigateTo(url) {
    window.location.href = url
}