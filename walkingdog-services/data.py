"""Interface to the database.
"""
# pylint: disable=


from . import model


def add_device(
        email: str,
        device_id: str,
        device_type: str):
    """Adds a device to the database.

    :param email: email of the user, used for identification.
    :type  email: str

    :param device_id: name given to the current device.
    :type  device_id: str

    :param device_type: model of the device or OS version.
    :type  device_type: str
    """


def remove_device(
        email: str,
        device_id: str):
    """Adds a device to the database.

    :param email: email of the user, used for identification.
    :type  email: str

    :param device_id: name given to the current device.
    :type  device_id: str
    """


def add_dog(
        email: str,
        dog: model.Dog):
    """Adds a dog to the database

    :param email: email of the user, used for identification.
    :type  email: str

    :param dog: the dog to add.
    :type  dog: model.Dog
    """


def remove_dog(
        email: str,
        dog: model.Dog):
    """Removes a dog to the database

    :param email: email of the user, used for identification.
    :type  email: str

    :param dog: the dog to remove.
    :type  dog: model.Dog
    """
