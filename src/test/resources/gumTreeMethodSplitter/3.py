
async def async_simple_no_typed(gh, original_issue, branch, backport_pr_number):
    """
    async doc
    """
    pass


@router.register("pull_request", action="opened")
@router.register("pull_request", action="edited")
async def async_schrecklich_typed(event: str, x: int , *args, **kwargs) -> int:

    def inner():
        pass

    return 42
