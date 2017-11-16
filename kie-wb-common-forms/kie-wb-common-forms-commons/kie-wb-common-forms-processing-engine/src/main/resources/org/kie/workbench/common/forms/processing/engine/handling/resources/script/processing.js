function FieldChangeExecutor() {
    this.execute = function (formField, newValue) {
                 alert("hi " + formField + " " + newValue);
                 formField.setError("New value: " + newValue);
               }
};

window.FieldChangeExecutor = new FieldChangeExecutor();
