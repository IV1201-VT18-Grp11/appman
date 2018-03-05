def dict_drop_none(full_dict):
    new_dict = {}
    for (key, value) in full_dict.items():
        if value is not None:
            new_dict[key] = value
    return new_dict
