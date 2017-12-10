"""Define the classes to use in the backend to model the information:

    - Device: model of the dog owner and device user.
      One User will be instanciated for each active device.

    - Dog: information about the dog.
      Each Dog will be instanciated once for multiple Devices.

    - Location: representation of the position of the Dog. The owners are
      assumed to be with them.

    - WalkRequest: message sent to another device to request to walk together.
      Its status can be PENDING, ACCEPTED, REFUSED.
"""
# pylint: disable=too-few-public-methods,function-redefined

from datetime import datetime
import attr


@attr.s
class Location:
    """Model of the position of the dog.
    """
    longitude: float = attr.ib()
    latitude: float = attr.ib()
    last_updated: datetime = attr.ib()

    def update(self, latitude: float, longitude: float):
        """Updates the location of the dog.

        :param latitude: Current latitude

        :type latitude: float

        :param longitude: Current latitude

        :type longitude: float
        """


class WalkRequest:
    """stub"""


@attr.s
class Dog:
    """Model of the dog being walked.
    """
    uuid: str = attr.ib()
    name: str = attr.ib()
    picture: str = attr.ib()
    rate: float = attr.ib()
    location: Location = attr.ib()
    masters: list = attr.ib()

    def transmit_walk_request(self, request: WalkRequest):
        """Transmits a received WalkRequest to all masters.

        :param request: request received that we will transmit
            to the owners.

        :type request: WalkRequest
        """


@attr.s
class WalkRequest:
    """Message sent to another device to request
    to walk together.
    """
    status: str = attr.ib(validator=attr.validators.in_([
        "PENDING",
        "ACCEPTED",
        "REFUSED"]))
    send_date: datetime = attr.ib()
    answer_date: datetime = attr.ib()
    to_: Dog = attr.ib()
    from_: Dog = attr.ib()


@attr.s
class Device:
    """Model of the device currently used by the dog owner.
    """
    email: str = attr.ib()
    device_id: str = attr.ib()
    device_type: str = attr.ib()
    favourites: list = attr.ib()

    def send_walk_request(self, dog: Dog):
        """Sends a request to a dog to walk together.

        :param dog: the dog with whom to walk.

        :type dog: Dog
        """

    def receive_walk_request(self, request: WalkRequest):
        """Transmits a walk request from our dog to the actual device.

        :param request: The request received from our dog.

        :type request: WalkRequest
        """

    def accept_walk_request(self, request: WalkRequest):
        """Refuses a walk request.

        :param request: The request received from our dog.

        :type request: WalkRequest
        """

    def refuse_walk_request(self, request: WalkRequest):
        """Accepts a walk request.

        :param request: The request received from our dog.

        :type request: WalkRequest
        """

    def add_dog(self, dog: Dog):
        """Links a new dog to the device.

        :param dog: The dog to add.

        :type dog: Dog
        """

    def remove_dog(self, dog: Dog):
        """Removes a dog linked to the device.

        :param dog: The dog to add.

        :type dog: Dog
        """

    def add_favourite(self, dog: Dog):
        """Adds a dog to our favourites.

        :param dog: The dog to add to favourites.

        :type dog: Dog
        """

    def remove_favourite(self, dog: Dog):
        """Removes a dog from our favourites.

        :param dog: The dog from our favourites.

        :type dog: Dog
        """

    def rate_dog(self, dog: Dog):
        """Rates a dog with which we have been walking.

        :param dog: The dog with whom we walk.

        :type dog: Dog
        """
